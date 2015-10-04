package org.marsik.elshelves.backend.interfaces;

import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.IdentifiedEntityInterface;

public interface Relinker {
    IdentifiedEntityInterface relink(IdentifiedEntityInterface e);
}
