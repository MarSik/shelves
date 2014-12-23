package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Purchase;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface PurchaseRepository extends GraphRepository<Purchase> {
	Purchase getPurchaseByUuid(UUID uuid);
}
