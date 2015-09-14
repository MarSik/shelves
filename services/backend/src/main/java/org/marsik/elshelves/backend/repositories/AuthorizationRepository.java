package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorizationRepository extends JpaRepository<Authorization, UUID> {
    Authorization findByUuid(UUID uuid);
}
