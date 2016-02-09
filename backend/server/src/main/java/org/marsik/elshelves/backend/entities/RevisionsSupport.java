package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.backend.services.UuidGenerator;

public interface RevisionsSupport<T extends IdentifiedEntity> {
    boolean isRevisionNeeded(UpdateableEntity update);
    T createRevision(UpdateableEntity update, UuidGenerator uuidGenerator, User performedBy);
    T getPreviousRevision();
    void setPreviousRevision(T revision);
}
