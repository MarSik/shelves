package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.TypeRepository;

public interface TypeService extends AbstractRestServiceIntf<TypeRepository, Type> {
    Type getUniqueTypeByNameAndFootprint(String name, String footprint, User currentUser) throws PermissionDenied;
}
