package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.NumericProperty;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface NumericPropertyRepository extends GraphRepository<NumericProperty> {
    Iterable<NumericProperty> findByOwner(User owner);
    NumericProperty findByUuid(UUID uuid);
}
