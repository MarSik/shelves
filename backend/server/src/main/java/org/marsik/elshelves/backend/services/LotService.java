package org.marsik.elshelves.backend.services;

import org.joda.time.DateTime;
import org.marsik.elshelves.backend.controllers.exceptions.EntityNotFound;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.controllers.exceptions.PermissionDenied;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.Lot;
import org.marsik.elshelves.backend.entities.PurchasedLot;
import org.marsik.elshelves.backend.entities.Source;
import org.marsik.elshelves.backend.entities.Transaction;
import org.marsik.elshelves.backend.entities.Type;
import org.marsik.elshelves.backend.entities.User;

import java.util.Collection;
import java.util.UUID;

public interface LotService {
    Collection<Lot> getAll(User currentUser);

    Lot get(UUID id, User currentUser) throws PermissionDenied, EntityNotFound;

    Lot deliverPurchasedLot(PurchasedLot newLot0, DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied,
            OperationNotPermitted;

    Lot deliverTypeInTransaction(Transaction transaction0, Type type0, Box location0, Long count,
                                 DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted;

    Lot deliverAdHoc(Source source0, Type type0, Box location0, Long count,
                     DateTime expiration, User currentUser) throws EntityNotFound, PermissionDenied, OperationNotPermitted;

    LotSplitResult update(Lot lot, Lot update, User currentUser) throws PermissionDenied, EntityNotFound, OperationNotPermitted;
}
