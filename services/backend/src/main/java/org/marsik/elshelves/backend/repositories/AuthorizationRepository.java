package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.entities.User;

public interface AuthorizationRepository extends BaseIdentifiedEntityRepository<Authorization> {
    Iterable<Authorization> findByOwner(User owner);
}
