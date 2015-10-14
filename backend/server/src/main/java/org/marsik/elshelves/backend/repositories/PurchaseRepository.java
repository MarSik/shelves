package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.User;

public interface PurchaseRepository extends BaseIdentifiedEntityRepository<Purchase> {
	Iterable<Purchase> findByTransactionOwner(User owner);
}
