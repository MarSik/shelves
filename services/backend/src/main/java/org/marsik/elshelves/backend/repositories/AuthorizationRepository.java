package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorizationRepository extends BaseIdentifiedEntityRepository<Authorization> {
    Iterable<Authorization> findByOwner(User owner);
}
