package org.marsik.elshelves.ember;

import com.fasterxml.jackson.annotation.JsonIdentityReference;

public class EmberPurge {
    String type;

    Object id;

    public EmberPurge() {
    }

    public EmberPurge(String type, Object id) {
        this.type = type;
        this.id = id;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
