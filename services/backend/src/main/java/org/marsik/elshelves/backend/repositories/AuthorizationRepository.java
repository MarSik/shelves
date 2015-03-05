package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Authorization;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface AuthorizationRepository extends GraphRepository<Authorization> {
    Authorization findByUuid(UUID uuid);
}
