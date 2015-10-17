package org.marsik.elshelves.backend.entities;

public interface UpdateableEntity extends OwnedEntityInterface {
    void updateFrom(UpdateableEntity update);
    Long getVersion();
}
