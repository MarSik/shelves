package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Requirement;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface RequirementRepository extends OwnedRepository<Requirement> {
	@Query("START u=node({0}) MATCH u -[:OWNS]-> (p:Project) -- (r:Requirement) RETURN DISTINCT r")
	@Override
	Iterable<Requirement> findByOwner(User owner);
}
