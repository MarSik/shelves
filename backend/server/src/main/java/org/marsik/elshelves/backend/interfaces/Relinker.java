package org.marsik.elshelves.backend.interfaces;

import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;

public interface Relinker {
    <T extends IdentifiedEntityInterface> T findExisting(T e);
}
