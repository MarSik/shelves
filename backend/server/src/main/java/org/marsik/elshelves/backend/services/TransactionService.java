package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.repositories.TransactionRepository;

public interface TransactionService extends AbstractRestServiceIntf<TransactionRepository, Transaction> {
}
