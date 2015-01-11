package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Project;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface ProjectRepository extends GraphRepository<Project> {
    Iterable<Project> findByOwner(User owner);
    Project findByUuid(UUID uuid);
}
