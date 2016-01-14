package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.Purchase;
import org.marsik.elshelves.backend.repositories.PurchaseRepository;

public interface PurchaseService extends AbstractRestServiceIntf<PurchaseRepository, Purchase> {
    void deleteEntity(Purchase entity) throws OperationNotPermitted;
}
