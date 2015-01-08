package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.apache.commons.beanutils.BeanUtils;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.repositories.OwnedRepository;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.Traversal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.support.Neo4jTemplate;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractRestService<R extends OwnedRepository<T>, T extends OwnedEntity, E extends AbstractEntityApiModel> {
    final R repository;
    final CachingConverter<T, E, UUID> dbToRest;
    final CachingConverter<E, T, UUID> restToDb;
    final UuidGenerator uuidGenerator;

	@Autowired
	RelinkService relinkService;

	@Autowired
	Neo4jTemplate neo4jTemplate;

    public AbstractRestService(R repository,
                               CachingConverter<T, E, UUID> dbToRest,
                               CachingConverter<E, T, UUID> restToDb,
                               UuidGenerator uuidGenerator) {
        this.repository = repository;
        this.dbToRest = dbToRest;
        this.restToDb = restToDb;
        this.uuidGenerator = uuidGenerator;
    }

    public R getRepository() {
        return repository;
    }

	public CachingConverter<T, E, UUID> getDbToRest() {
		return dbToRest;
	}

	public CachingConverter<E, T, UUID> getRestToDb() {
		return restToDb;
	}

	public UuidGenerator getUuidGenerator() {
		return uuidGenerator;
	}

	protected Iterable<T> getAllEntities(User currentUser) {
        Iterable<T> result = repository.findByOwner(currentUser);
		return result;
    }

    protected T getSingleEntity(UUID uuid) {
        return repository.findByUuid(uuid);
    }

	protected int conversionDepth() {
		return 1;
	}

    protected T createEntity(E dto, User currentUser) {
        T created = restToDb.convert(dto, conversionDepth(), new THashMap<UUID, Object>());
		created = relinkService.relink(created);
        created.setUuid(uuidGenerator.generate());
        created.setOwner(currentUser);
        return created;
    }

	protected void deleteEntity(T entity) throws OperationNotPermitted {
		repository.delete(entity);
	}

    protected T updateEntity(T entity, T update) throws IllegalAccessException, InvocationTargetException, OperationNotPermitted {
		update = relinkService.relink(update);

		PropertyDescriptor[] properties;
		try {
			properties = Introspector.getBeanInfo(update.getClass()).getPropertyDescriptors();
		} catch (IntrospectionException ex) {
			ex.printStackTrace();
			return entity;
		}

		// For all update properties with getter..
		for (PropertyDescriptor f: properties) {
			Method getter = f.getReadMethod();
			if (getter == null) {
				continue;
			}

			// Update all writable collections in entity using
			// entity.clear(); entity.addAll(items) so the aspected
			// neo4j relationships are maintained properly
			if (Collection.class.isAssignableFrom(f.getPropertyType())) {
				try {
					Collection<Object> items = (Collection<Object>)getter.invoke(update);
					((Collection<Object>)getter.invoke(entity)).clear();
					((Collection<Object>)getter.invoke(entity)).addAll(items);
				} catch (InvocationTargetException | IllegalAccessException ex) {
					ex.printStackTrace();
				}

			// Skip read-only collections
			} else if (Iterable.class.isAssignableFrom(f.getPropertyType())) {
				continue;

			// Update all scalar properties
			} else {
				try {
					Object value = getter.invoke(entity);
					Method setter = f.getWriteMethod();
					if (setter != null) {
						setter.invoke(entity, value);
					}
				} catch (InvocationTargetException | IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}

        return update;
    }

    public Collection<E> getAllItems(User currentUser) {
        Set<E> dtos = new THashSet<>();
        Map<UUID, Object> cache = new THashMap<>();
        for (T entity: getAllEntities(currentUser)) {
            dtos.add(dbToRest.convert(entity, conversionDepth(), cache));
        }
        return dtos;
    }

    public E create(E dto, User currentUser) throws OperationNotPermitted {
        T created = createEntity(dto, currentUser);
        repository.save(created);
        return dbToRest.convert(created, conversionDepth(), new THashMap<UUID, Object>());
    }

    public E get(UUID uuid, User currentUser) throws PermissionDenied, EntityNotFound {
        T one = getSingleEntity(uuid);

		if (one == null) {
			throw new EntityNotFound();
		}

        if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        return dbToRest.convert(one, conversionDepth(), new THashMap<UUID, Object>());
    }

    public boolean delete(UUID uuid, User currentUser) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
        T one = getSingleEntity(uuid);

		if (one == null) {
			throw new EntityNotFound();
		}

        if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        deleteEntity(one);
        return true;
    }

    public E update(UUID uuid, E dto, User currentUser) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
        T one = getSingleEntity(uuid);

		if (one == null) {
			throw new EntityNotFound();
		}

		if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        try {
			T update = restToDb.convert(dto, 2, new THashMap<UUID, Object>());
            one = updateEntity(one, update);
        } catch (InvocationTargetException|IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return dbToRest.convert(one, conversionDepth(), new THashMap<UUID, Object>());
    }
}
