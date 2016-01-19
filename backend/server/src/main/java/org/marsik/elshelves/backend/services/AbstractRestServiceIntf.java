package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.app.hystrix.CircuitBreaker;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.UpdateableEntity;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.BaseIdentifiedEntityRepository;

import java.util.Collection;
import java.util.UUID;

public interface AbstractRestServiceIntf<R extends BaseIdentifiedEntityRepository<T>, T extends UpdateableEntity> {
    R getRepository();

    @CircuitBreaker Collection<T> getAllItems(User currentUser);

    @CircuitBreaker T create(T entity, User currentUser) throws OperationNotPermitted;

    @CircuitBreaker T get(UUID uuid, User currentUser) throws PermissionDenied, EntityNotFound;

    @CircuitBreaker boolean delete(UUID uuid, User currentUser) throws BaseRestException;

    @CircuitBreaker T update(T update, User currentUser) throws BaseRestException;

    void flush();
}
