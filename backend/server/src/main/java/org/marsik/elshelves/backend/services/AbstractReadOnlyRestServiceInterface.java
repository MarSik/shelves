package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.app.hystrix.CircuitBreaker;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.OwnedEntity;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.User;

import java.util.Collection;
import java.util.UUID;

public interface AbstractReadOnlyRestServiceInterface<T extends OwnedEntityInterface> {
    Collection<T> getAllItems(User currentUser);

    T get(UUID uuid, User currentUser) throws PermissionDenied, EntityNotFound;
}
