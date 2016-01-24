package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.joda.time.DateTime;
import org.marsik.elshelves.backend.app.hystrix.CircuitBreaker;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.controllers.exceptions.UpdateConflict;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.RevisionsSupport;
import org.marsik.elshelves.backend.entities.UpdateableEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractRestService<R extends BaseIdentifiedEntityRepository<T>, T extends UpdateableEntity> extends AbstractReadOnlyRestService<R, T>
        implements AbstractRestServiceIntf<R, T> {
    final UuidGenerator uuidGenerator;

	@Autowired
	RelinkService relinkService;

    @PersistenceContext
    EntityManager entityManager;

    public AbstractRestService(R repository,
                               UuidGenerator uuidGenerator) {
        super(repository);
        this.uuidGenerator = uuidGenerator;
    }

    public UuidGenerator getUuidGenerator() {
		return uuidGenerator;
	}

    protected T createEntity(T entity, User currentUser) {
        Relinker relinkContext = relinkService.newRelinker();
        relinkContext
                .currentUser(currentUser)
                .addToCache(entity)
                .fixOwner(entity, currentUser)
                .fixUuid(entity);

        entity.setLastModified(new DateTime());
        entity.relink(relinkContext);

        return entity;
    }

	public void deleteEntity(T entity) throws OperationNotPermitted {
		repository.delete(entity);
	}

    @SuppressWarnings("unchecked")
    protected T updateEntity(T entity, T update, User currentUser) throws IllegalAccessException, InvocationTargetException, OperationNotPermitted {
        if (!(entity instanceof UpdateableEntity)) {
            throw new OperationNotPermitted();
        }

        IdentifiedEntity revision = null;

        if (entity instanceof RevisionsSupport
                && ((RevisionsSupport) entity).isRevisionNeeded(update)) {
            revision = ((RevisionsSupport) entity).createRevision(uuidGenerator, currentUser);
        }

        entity.updateFrom(update);

        Relinker relinkContext = relinkService.newRelinker();
        relinkContext
                .currentUser(currentUser)
                .addToCache(currentUser)
                .addToCache(entity);

        if (entity instanceof RevisionsSupport
                && revision != null) {
            ((RevisionsSupport) entity).setPreviousRevision(revision);
        }

        entity.relink(relinkContext);

        return entity;
    }

    @Override @CircuitBreaker
    public T create(T entity, User currentUser) throws OperationNotPermitted {
        UUID expectedUuid = entity.getId();
        entity = createEntity(entity, currentUser);
        if (!entity.getId().equals(expectedUuid)
                && expectedUuid != null) {
            throw new OperationNotPermitted();
        }

        entity.setCreated(new DateTime());
        entity = save(entity);
        return entity;
    }

    @Override @CircuitBreaker
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

    @Override @CircuitBreaker
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
        return saveOrUpdate(entity);
    }

    protected <E extends IdentifiedEntityInterface> E saveOrUpdate(E entity) {
        if (entity == null) {
            return null;
        }

        if (entity.isNew()) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }

        return entity;
    }

    @Override public void flush() {
        repository.flush();
    }
}
