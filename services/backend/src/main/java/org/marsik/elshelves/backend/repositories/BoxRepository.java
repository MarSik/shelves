package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoxRepository extends JpaRepository<Box, UUID> {
    Iterable<Box> findByOwner(User owner);
    Box findByUuid(UUID uuid);
}
