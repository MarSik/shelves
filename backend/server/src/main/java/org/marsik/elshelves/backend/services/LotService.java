package org.marsik.elshelves.backend.services;

import org.joda.time.DateTime;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.marsik.elshelves.backend.entities.User;

import java.util.Collection;
import java.util.UUID;

public interface LotService {
    Collection<Lot> getAll(User currentUser);

    Lot get(UUID id, User currentUser) throws PermissionDenied, EntityNotFound;

    Lot delivery(PurchasedLot newLot0, DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied,
            OperationNotPermitted;

    <T extends Lot> LotSplitResult<T> update(T lot, T update, User currentUser) throws PermissionDenied, EntityNotFound, OperationNotPermitted;
}
