package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.backend.interfaces.Relinker;

public interface RelinkableEntity {
    void relink(Relinker relinker);
}
