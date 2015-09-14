package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
	@Query("START u=node({0}) MATCH u -[:OWNS]-> (l:Lot) -- (p:Purchase) RETURN DISTINCT p")
	Iterable<Purchase> findByOwner(User owner);
    Purchase findByUuid(UUID uuid);
}
