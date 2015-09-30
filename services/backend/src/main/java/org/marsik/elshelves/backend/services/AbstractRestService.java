package org.marsik.elshelves.backend.services;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.PartOfUpdate;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractRestService<R extends BaseIdentifiedEntityRepository<T>, T extends OwnedEntityInterface> {
    final R repository;
    final UuidGenerator uuidGenerator;

	@Autowired
	RelinkService relinkService;

    public AbstractRestService(R repository,
                               UuidGenerator uuidGenerator) {
        this.repository = repository;
        this.uuidGenerator = uuidGenerator;
    }

    public R getRepository() {
        return repository;
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

    protected T createEntity(T entity, User currentUser) {
		entity = relinkService.relink(entity);
        entity.setId(uuidGenerator.generate());
        entity.setOwner(currentUser);
        entity.setLastModified(new DateTime());
        return entity;
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

    public Collection<T> getAllItems(User currentUser) {
        Set<T> dtos = new THashSet<>();
        for (T entity: getAllEntities(currentUser)) {
            dtos.add(entity);
        }
        return dtos;
    }

    public T create(T entity, User currentUser) throws OperationNotPermitted {
        entity = createEntity(entity, currentUser);
        entity.setCreated(new DateTime());
        entity = repository.save(entity);
        return entity;
    }

    public T get(UUID uuid, User currentUser) throws PermissionDenied, EntityNotFound {
        T one = getSingleEntity(uuid);

		if (one == null) {
			throw new EntityNotFound();
		}

        if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        return one;
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

    public T update(T update, User currentUser) throws PermissionDenied, OperationNotPermitted, EntityNotFound {
        T one = getSingleEntity(update.getId());

		if (one == null) {
			throw new EntityNotFound();
		}

		if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        try {
            one = updateEntity(one, update, currentUser);
            one.setLastModified(new DateTime());

            // Introspection based updater breaks the aspected behaviour
            // so it is necessary to resave the updated object here
            one = repository.save(one);
        } catch (InvocationTargetException|IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return one;
    }
}
