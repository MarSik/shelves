package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends BaseOwnedEntityRepository<Transaction> {
}
