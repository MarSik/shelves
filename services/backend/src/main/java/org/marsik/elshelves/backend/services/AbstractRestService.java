package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.PartOfUpdate;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.CachingConverter;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractRestService<R extends BaseIdentifiedEntityRepository<T>, T extends OwnedEntityInterface, E extends AbstractEntityApiModel> {
    final R repository;
    final CachingConverter<T, E, UUID> dbToRest;
    final CachingConverter<E, T, UUID> restToDb;
    final UuidGenerator uuidGenerator;

	@Autowired
	RelinkService relinkService;

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

	protected abstract Iterable<T> getAllEntities(User currentUser);

    protected T getSingleEntity(UUID uuid) {
        return repository.findById(uuid);
    }

	protected int conversionDepth() {
		return 1;
	}

    protected T createEntity(E dto, User currentUser) {
        T created = restToDb.convert(dto, conversionDepth(), new THashMap<UUID, Object>());
		created = relinkService.relink(created);
        created.setId(uuidGenerator.generate());
        created.setOwner(currentUser);
        created.setLastModified(new DateTime());
        return created;
    }

	protected void deleteEntity(T entity) throws OperationNotPermitted {
		repository.delete(entity);
	}

    protected T updateEntity(T entity, T update, User currentUser) throws IllegalAccessException, InvocationTargetException, OperationNotPermitted {
		update = relinkService.relink(update, currentUser, entity);
        Set<Object> modified = new THashSet<>();

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

			if (getter.getAnnotation(PartOfUpdate.class) == null) {
				continue;
			}

			// Update all writable collections in entity using
			// entity.clear(); entity.addAll(items) so the aspected
			// neo4j relationships are maintained properly
			if (Collection.class.isAssignableFrom(f.getPropertyType())) {
				try {
					Collection<Object> items = (Collection<Object>)getter.invoke(update);

                    // Track modifications
                    for (Object item: items) {
                        if (OwnedEntity.class.isAssignableFrom(item.getClass())) {
                            modified.add(item);
                        }
                    }
                    for (Object item: (Collection<Object>) getter.invoke(entity)) {
                        if (OwnedEntity.class.isAssignableFrom(item.getClass())) {
                            modified.add(item);
                        }
                    }

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
					Object value = getter.invoke(update);
					Method setter = f.getWriteMethod();

                    // Track modifications
                    if (OwnedEntity.class.isAssignableFrom(f.getPropertyType())) {
                        modified.add(value);
                        modified.add(getter.invoke(entity));
                    }

					if (setter != null) {
						setter.invoke(entity, value);
					}
				} catch (InvocationTargetException | IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}


        // Mark all old and new elements as modified
        DateTime modificationDate = new DateTime();
        modified.add(entity);
        for (Object m: modified) {
            if (m != null && m instanceof OwnedEntity) {
                ((OwnedEntity) m).setLastModified(modificationDate);
            }
        }

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

		if (!one.canBeDeleted()) {
			throw new OperationNotPermitted();
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
			// The REST entity does not contain id during PUT, because that is
			// provided by the URL
			update.setId(uuid);
            one = updateEntity(one, update, currentUser);

            // Introspection based updater breaks the aspected behaviour
            // so it is necessary to resave the updated object here
            repository.save(one);
        } catch (InvocationTargetException|IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return dbToRest.convert(one, conversionDepth(), new THashMap<UUID, Object>());
    }
}
