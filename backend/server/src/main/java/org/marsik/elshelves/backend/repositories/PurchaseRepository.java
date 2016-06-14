package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseRepository extends BaseIdentifiedEntityRepository<Purchase> {
	Iterable<Purchase> findByTransactionOwner(User owner);

	@Query("SELECT p FROM OwnedEntity o, Purchase p, Lot l0, PurchasedLot l WHERE o.dbId = l.dbId AND o.owner = ?1 AND l.purchase = p AND l0.dbId = l.dbId AND l0.type = ?2 AND l0.valid = true GROUP BY p HAVING sum(l0.count) < p.count")
	Iterable<Purchase> findUndelivered(User owner, Type type);
}
