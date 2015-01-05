package org.marsik.elshelves.backend.entities;

import java.util.UUID;

public interface OwnedEntity {
	// Provided by AspectJ-ized NodeEntity
	Long getNodeId();

    UUID getUuid();
    void setUuid(UUID uuid);

    User getOwner();
    void setOwner(User user);
}
