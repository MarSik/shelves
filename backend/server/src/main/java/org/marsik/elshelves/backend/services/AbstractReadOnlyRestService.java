package org.marsik.elshelves.backend.services;

import gnu.trove.set.hash.THashSet;
import org.marsik.elshelves.backend.app.hystrix.CircuitBreaker;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.UpdateableEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractReadOnlyRestService<R extends BaseIdentifiedEntityRepository<T>, T extends OwnedEntityInterface> implements AbstractReadOnlyRestServiceInterface<T> {
    @Autowired
    final R repository;

    public AbstractReadOnlyRestService(R repository) {
        this.repository = repository;
    }

    public R getRepository() {
        return repository;
    }

    protected abstract Iterable<T> getAllEntities(User currentUser);

    protected T getSingleEntity(UUID uuid) {
return repository.findById(uuid);
}

    protected int conversionDepth() {
        return 1;
    }

    @Override
    @CircuitBreaker
    public Collection<T> getAllItems(User currentUser) {
        Set<T> dtos = new THashSet<>();
        for (T entity: getAllEntities(currentUser)) {
            dtos.add(entity);
        }
        return dtos;
    }

    @Override
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
}
