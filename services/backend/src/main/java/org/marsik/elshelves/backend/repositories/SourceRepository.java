package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SourceRepository extends JpaRepository<Source, UUID> {
    Iterable<Source> findByOwner(User owner);
    Source findByUuid(UUID uuid);
}
