package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;

public interface UpdateableEntity extends OwnedEntityInterface {
    void updateFrom(UpdateableEntity update) throws OperationNotPermitted;
    Long getVersion();
}
