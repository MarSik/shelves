package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Source;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface SourceRepository extends GraphRepository<Source> {
	Source getSourceByUuid(UUID uuid);
}
