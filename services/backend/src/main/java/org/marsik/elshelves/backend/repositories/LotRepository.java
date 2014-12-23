package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Lot;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface LotRepository extends GraphRepository<Lot> {
	Lot getLotByUuid(UUID uuid);
}
