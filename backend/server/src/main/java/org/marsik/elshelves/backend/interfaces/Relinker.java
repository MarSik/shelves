package org.marsik.elshelves.backend.interfaces;

import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.OwnedEntityInterface;
import org.marsik.elshelves.backend.entities.Sku;
import org.marsik.elshelves.backend.entities.User;

import java.util.UUID;

public interface Relinker {
    <T extends IdentifiedEntityInterface> T findExisting(T e);

    Sku findExistingSku(Sku sku);

    Relinker currentUser(User user);

    Relinker addToCache(UUID id, IdentifiedEntityInterface entity);

    Relinker addToCache(IdentifiedEntityInterface entity);

    IdentifiedEntityInterface get(UUID id);

    IdentifiedEntityInterface relink(IdentifiedEntityInterface value);

    <T extends IdentifiedEntityInterface> T fixUuid(T value);

    Relinker fixOwner(OwnedEntityInterface item, User user);

    Relinker ensureOwner(OwnedEntityInterface item, User user);
}
