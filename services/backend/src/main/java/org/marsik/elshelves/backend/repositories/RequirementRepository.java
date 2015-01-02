package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Requirement;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface RequirementRepository extends GraphRepository<Requirement> {
	Requirement getRequirementByUuid(UUID uuid);
}
