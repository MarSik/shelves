package org.marsik.elshelves.backend.entities;

public interface OwnedEntityInterface extends IdentifiedEntityInterface {
    @PartOfUpdate
    User getOwner();

    void setOwner(User owner);

    boolean canBeDeleted();

    boolean canBeUpdated();
}
