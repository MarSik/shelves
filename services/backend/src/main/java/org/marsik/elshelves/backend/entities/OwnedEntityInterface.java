package org.marsik.elshelves.backend.entities;

public interface OwnedEntityInterface extends IdentifiedEntityInterface {
    User getOwner();

    void setOwner(User owner);

    boolean canBeDeleted();

    boolean canBeUpdated();
}
