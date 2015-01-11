package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface LotRepository extends GraphRepository<Lot> {
    Iterable<Lot> findByOwner(User owner);
    Lot findByUuid(UUID uuid);
}
