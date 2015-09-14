package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TypeRepository extends JpaRepository<Type, UUID> {
    Iterable<Type> findByOwner(User owner);
    Type findByUuid(UUID uuid);

    @Query("START user=node({2}) MATCH (t:Type) -[:HAS_FOOTPRINT]-> (fp:Footprint), t <-[:OWNS]- user, fp <-[:OWNS]- user WHERE t.name = {0} AND fp.name = {1} RETURN t")
    Result<Type> findByNameAndFootprintName(String name, String footprintName, User user);
}
