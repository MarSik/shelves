package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Box;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface BoxRepository extends GraphRepository<Box> {
    Box getBoxByUuid(UUID uuid);
}
