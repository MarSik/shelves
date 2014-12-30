package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Transaction;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface TransactionRepository extends GraphRepository<Transaction> {
	Transaction getTransactionByUuid(UUID uuid);
}
