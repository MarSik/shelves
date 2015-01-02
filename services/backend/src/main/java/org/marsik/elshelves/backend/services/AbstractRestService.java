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

public abstract class AbstractRestService<R extends GraphRepository<T>, T extends OwnedEntity, E extends AbstractEntityApiModel> {
    final R repository;
    final CachingConverter<T, E, UUID> dbToRest;
    final CachingConverter<E, T, UUID> restToDb;
    final UuidGenerator uuidGenerator;

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
        return repository.findAll();
    }

    protected T getSingleEntity(UUID uuid) {
        return repository.findBySchemaPropertyValue("uuid", uuid);
    }

	protected <E extends OwnedEntity> E getRelinked(E value) {
		return (E)neo4jTemplate.findByIndexedValue(value.getClass(), "uuid", value.getUuid()).single();
	}

	/**
	 * This method is used to realign entities coming from external sources
	 * like REST based web application with the entities stored in the database.
	 *
	 * The linking is done using the uuid property values.
	 *
	 * Modus operandi:
	 *
	 * Find all relationship properties and make sure they reference existing
	 * database entities by querying the uuid index.
	 * If the property does not contain uuid (happens with
	 * nested rest fields), call relink on the the property contents to relink
	 * the nested properties.
	 *
	 * @param entity Entity to relink with the daatabase
	 * @return
	 */
	protected <E extends OwnedEntity> E relink(E entity) {
		PropertyDescriptor[] properties;
		try {
			properties = Introspector.getBeanInfo(entity.getClass()).getPropertyDescriptors();
		} catch (IntrospectionException ex) {
			ex.printStackTrace();
			return entity;
		}

		for (PropertyDescriptor f: properties) {
			Method getter = f.getReadMethod();
			if (getter == null) {
				continue;
			}

			if (OwnedEntity.class.isAssignableFrom(f.getPropertyType())) {
				try {
					OwnedEntity value = (OwnedEntity) getter.invoke(entity);
					Method setter = f.getWriteMethod();

					if (value != null && setter != null) {
						if (value.getUuid() != null) {
							OwnedEntity v = getRelinked(value);
							assert v != null && v.getClass().equals(value.getClass());
							setter.invoke(entity, v);
						} else {
							relink(value);
						}
					}
				} catch (InvocationTargetException | IllegalAccessException ex) {
					ex.printStackTrace();
				}
			} else if (Collection.class.isAssignableFrom(f.getPropertyType())) {
				try {
					Collection<OwnedEntity> newItems = new ArrayList<OwnedEntity>();
					Collection<OwnedEntity> items = (Collection<OwnedEntity>)getter.invoke(entity);
					for (OwnedEntity item: items) {
						if (item.getUuid() == null) {
							newItems.add(item);
							relink(item);
						} else {
							OwnedEntity v = getRelinked(item);
							assert v != null && v.getClass().equals(item.getClass());
							newItems.add(v);
						}
					}
					items.clear();
					items.addAll(newItems);
				} catch (InvocationTargetException | IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}

		return entity;
	}

	protected int conversionDepth() {
		return 1;
	}

    protected T createEntity(E dto, User currentUser) {
        T created = restToDb.convert(dto, conversionDepth(), new THashMap<UUID, Object>());
		created = relink(created);
        created.setUuid(uuidGenerator.generate());
        created.setOwner(currentUser);
        return created;
    }

	protected void deleteEntity(T entity) throws OperationNotPermitted {
		repository.delete(entity);
	}

    protected T updateEntity(T entity, E dto) throws IllegalAccessException, InvocationTargetException, OperationNotPermitted {
        BeanUtils.copyProperties(entity, dto);
		entity = relink(entity);
        return entity;
    }

    public Collection<E> getAllItems(User currentUser) {
        Set<E> dtos = new THashSet<>();
        Map<UUID, Object> cache = new THashMap<>();
        for (T entity: getAllEntities(currentUser)) {
            dtos.add(dbToRest.convert(entity, conversionDepth(), cache));
        }
        return dtos;
    }

    public E create(E dto, User currentUser) {
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
            updateEntity(one, dto);
        } catch (InvocationTargetException|IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return dbToRest.convert(one, conversionDepth(), new THashMap<UUID, Object>());
    }
}
