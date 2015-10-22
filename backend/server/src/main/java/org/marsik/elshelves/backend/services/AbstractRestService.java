package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.backend.app.hystrix.CircuitBreaker;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.controllers.exceptions.UpdateConflict;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.UpdateableEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractRestService<R extends BaseIdentifiedEntityRepository<T>, T extends UpdateableEntity> {
    final R repository;
    final UuidGenerator uuidGenerator;

	@Autowired
	RelinkService relinkService;

    @PersistenceContext
    EntityManager entityManager;

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
        RelinkService.RelinkContext relinkContext = relinkService.newRelinker();
        relinkContext.addToCache(entity);
        relinkContext.fixOwner(entity, currentUser);

        entity.setId(uuidGenerator.generate());
        entity.setLastModified(new DateTime());
        entity.relink(relinkContext);

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

        ((UpdateableEntity)entity).updateFrom(update);

        RelinkService.RelinkContext relinkContext = relinkService.newRelinker();
        relinkContext
                .addToCache(currentUser)
                .addToCache(entity);

        entity.relink(relinkContext);

        return entity;
    }

    @CircuitBreaker
    public Collection<T> getAllItems(User currentUser) {
        Set<T> dtos = new THashSet<>();
        for (T entity: getAllEntities(currentUser)) {
            dtos.add(entity);
        }
        return dtos;
    }

    @CircuitBreaker
    public T create(T entity, User currentUser) throws OperationNotPermitted {
        entity = createEntity(entity, currentUser);
        entity.setCreated(new DateTime());
        entity = save(entity);
        return entity;
    }

    @CircuitBreaker
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

    @CircuitBreaker
    public boolean delete(UUID uuid, User currentUser) throws BaseRestException {
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

    @CircuitBreaker
    public T update(T update, User currentUser) throws BaseRestException {
        T one = getSingleEntity(update.getId());

		if (one == null) {
			throw new EntityNotFound();
		}

		if (!one.getOwner().equals(currentUser)) {
            throw new PermissionDenied();
        }

        if (one.getVersion() != null && !one.getVersion().equals(update.getVersion())) {
            throw new UpdateConflict();
        }

        if (update.getVersion() != null && !update.getVersion().equals(one.getVersion())) {
            throw new UpdateConflict();
        }

        try {
            one = updateEntity(one, update, currentUser);
            one.setLastModified(new DateTime());

            one = save(one);
        } catch (InvocationTargetException|IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return one;
    }

    protected T save(T entity) {
        return repository.save(entity);
    }

    protected <E extends IdentifiedEntityInterface> E saveOrUpdate(E entity) {
        if (entity == null) {
            return null;
        }

        if (entity.isNew()) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }

        return entity;
    }

    public void flush() {
        repository.flush();
    }
}
