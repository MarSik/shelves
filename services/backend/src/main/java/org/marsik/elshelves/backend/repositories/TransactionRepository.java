package org.marsik.elshelves.backend.repositories;

import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.UUID;

public interface TransactionRepository extends GraphRepository<Transaction> {
    Iterable<Transaction> findByOwner(User owner);
    Transaction findByUuid(UUID uuid);
}
