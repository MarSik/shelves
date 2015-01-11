package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Document;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface DocumentRepository extends GraphRepository<Document> {
    Iterable<Document> findByOwner(User owner);
    Document findByUuid(UUID uuid);
}
