package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PurchaseRepository extends BaseIdentifiedEntityRepository<Purchase> {
	Iterable<Purchase> findByTransactionOwner(User owner);
}
