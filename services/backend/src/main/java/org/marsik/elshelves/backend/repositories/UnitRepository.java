package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Unit;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface UnitRepository extends GraphRepository<Unit> {
	Iterable<Unit> findByOwner(User owner);
	Unit findByUuid(UUID uuid);
}
