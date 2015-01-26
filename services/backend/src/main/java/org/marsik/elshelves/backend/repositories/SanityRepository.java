package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

public interface SanityRepository extends GraphRepository<NamedEntity> {
    @Query("MATCH (t:Type) WHERE NOT (t) -- (:Group) RETURN t")
    Iterable<Type> findOrphanedTypes();
}
