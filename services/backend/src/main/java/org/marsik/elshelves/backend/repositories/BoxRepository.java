package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Box;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface BoxRepository extends GraphRepository<Box> {
}
