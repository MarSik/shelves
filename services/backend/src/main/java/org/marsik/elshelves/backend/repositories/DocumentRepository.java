package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Document;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface DocumentRepository extends GraphRepository<Document> {
	Document getDocumentByUuid(UUID uuid);
}
