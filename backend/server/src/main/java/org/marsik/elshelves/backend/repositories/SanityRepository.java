package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.NamedEntity;
import org.marsik.elshelves.backend.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SanityRepository extends JpaRepository<NamedEntity, UUID> {
    @Query("SELECT t FROM Type t WHERE t.groups IS EMPTY")
    Iterable<Type> findOrphanedTypes();
}
