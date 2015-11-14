package org.marsik.elshelves.backend.interfaces;

import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;
import org.marsik.elshelves.backend.entities.Sku;
import org.marsik.elshelves.backend.entities.User;

public interface Relinker {
    <T extends IdentifiedEntityInterface> T findExisting(T e);
    Sku findExistingSku(Sku sku);
}
