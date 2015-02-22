package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface TypeRepository extends GraphRepository<Type> {
    Iterable<Type> findByOwner(User owner);
    Type findByUuid(UUID uuid);

    @Query("MATCH (t:Type) -[:HAS_FOOTPRINT]-> (fp:Footprint) WHERE t.name = {0} AND fp.name = {1} RETURN t")
    Type findByNameAndFootprintName(String name, String footprintName);
}
