package org.marsik.elshelves.backend.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.UpdateableEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractRestService<R extends BaseIdentifiedEntityRepository<T>, T extends UpdateableEntity> {
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

    @SuppressWarnings("unchecked")
    protected T updateEntity(T entity, T update, User currentUser) throws IllegalAccessException, InvocationTargetException, OperationNotPermitted {
        if (!(entity instanceof UpdateableEntity)) {
            throw new OperationNotPermitted();
        }

		update = relinkService.relink(update, currentUser, entity);
        ((UpdateableEntity)entity).updateFrom(update);

        return entity;
    }

    public Collection<T> getAllItems(User currentUser) {
        Set<T> dtos = new THashSet<>();
        for (T entity: getAllEntities(currentUser)) {
            dtos.add(entity);
        }
        return dtos;
    }

    @HystrixCommand
    public T create(T entity, User currentUser) throws OperationNotPermitted {
        entity = createEntity(entity, currentUser);
        entity.setCreated(new DateTime());
        entity = repository.save(entity);
        return entity;
    }

    @HystrixCommand
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

    @HystrixCommand
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

    @HystrixCommand
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
