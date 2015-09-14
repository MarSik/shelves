package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Footprint;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FootprintRepository extends JpaRepository<Footprint, UUID> {
    Iterable<Footprint> findByOwner(User owner);
    Footprint findByUuid(UUID uuid);
}
