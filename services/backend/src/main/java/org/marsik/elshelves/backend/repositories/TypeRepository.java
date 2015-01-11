package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface TypeRepository extends GraphRepository<Type> {
    Iterable<Type> findByOwner(User owner);
    Type findByUuid(UUID uuid);
}
