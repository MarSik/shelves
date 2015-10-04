package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.backend.interfaces.Relinker;

public interface IdentifiedEntityInterface extends RelinkableEntity {
    java.util.UUID getId();

    Long getDbId();

    org.joda.time.DateTime getLastModified();

    void setId(java.util.UUID id);

    void setLastModified(org.joda.time.DateTime lastModified);

    void setCreated(org.joda.time.DateTime lastModified);

    boolean isNew();

    void relink(Relinker relinker);
}
