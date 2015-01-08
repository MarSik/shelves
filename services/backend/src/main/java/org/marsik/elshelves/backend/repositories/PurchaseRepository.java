package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface PurchaseRepository extends OwnedRepository<Purchase> {
	@Query("START u=node({0}) MATCH u -[:OWNS]-> (l:Lot) -- (p:Purchase) RETURN DISTINCT p")
	@Override
	Iterable<Purchase> findByOwner(User owner);
}
