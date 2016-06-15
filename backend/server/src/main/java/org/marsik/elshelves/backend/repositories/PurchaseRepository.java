package org.marsik.elshelves.backend.repositories;

import java.util.UUID;

import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseRepository extends BaseIdentifiedEntityRepository<Purchase> {
	Iterable<Purchase> findByTransactionOwner(User owner);

	@Query("SELECT p.id FROM Purchase p LEFT OUTER JOIN p.lots l WHERE p.owner = ?1 AND p.type = ?2 AND coalesce(l.valid,true)=true GROUP BY p.id, p.count HAVING coalesce(sum(l.count),0) < p.count")
	Iterable<UUID> findUndelivered(User owner, Type type);
}
