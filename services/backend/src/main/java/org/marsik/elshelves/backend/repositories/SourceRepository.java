package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface SourceRepository extends GraphRepository<Source> {
    Iterable<Source> findByOwner(User owner);
    Source findByUuid(UUID uuid);
}
