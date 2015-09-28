package org.marsik.elshelves.backend.entities;

public interface IdentifiedEntityInterface {
    java.util.UUID getId();

    Long getDbId();

    org.joda.time.DateTime getLastModified();

    void setId(java.util.UUID id);

    void setLastModified(org.joda.time.DateTime lastModified);

    boolean isNew();
}
