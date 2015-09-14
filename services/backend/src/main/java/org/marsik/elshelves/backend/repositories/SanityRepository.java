package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SanityRepository extends JpaRepository<NamedEntity, UUID> {
    @Query("MATCH (t:Type) WHERE NOT (t) -- (:Group) RETURN t")
    Iterable<Type> findOrphanedTypes();
}
