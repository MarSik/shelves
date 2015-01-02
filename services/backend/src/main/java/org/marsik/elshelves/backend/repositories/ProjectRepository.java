package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Project;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface ProjectRepository extends GraphRepository<Project> {
	Project getProjectByUuid(UUID uuid);
}
