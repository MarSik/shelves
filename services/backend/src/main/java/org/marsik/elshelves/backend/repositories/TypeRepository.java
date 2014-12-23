package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Type;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface TypeRepository extends GraphRepository<Type> {
	Type getTypeByUuid(UUID uuid);
}
