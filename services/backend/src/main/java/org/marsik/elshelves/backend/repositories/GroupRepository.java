package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Group;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface GroupRepository extends GraphRepository<Group> {
	Group getGroupByUuid(UUID uuid);
}
