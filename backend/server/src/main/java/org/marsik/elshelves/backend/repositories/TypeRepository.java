package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.Query;

public interface TypeRepository extends BaseOwnedEntityRepository<Type> {
    @Query("SELECT t from Type t, Footprint f WHERE t.owner = ?3 AND t.name = ?1 AND f.type = t AND f.name = ?2")
    Iterable<Type> findByNameAndFootprintName(String name, String footprintName, User user);
}
