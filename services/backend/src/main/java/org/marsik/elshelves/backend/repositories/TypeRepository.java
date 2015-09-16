package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TypeRepository extends JpaRepository<Type, UUID> {
    Iterable<Type> findByOwner(User owner);
    Type findByUuid(UUID uuid);

    @Query("SELECT t from Type t, Footprint f WHERE t.owner = ?3 AND t.name = ?1 AND f.type = t AND f.name = ?2")
    Iterable<Type> findByNameAndFootprintName(String name, String footprintName, User user);
}
