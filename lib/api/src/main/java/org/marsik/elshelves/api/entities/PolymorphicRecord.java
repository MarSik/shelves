package org.marsik.elshelves.api.entities;

import java.util.UUID;

public class PolymorphicRecord extends AbstractEntityApiModel {
    String type;

    public PolymorphicRecord() {
    }

    public PolymorphicRecord(UUID id) {
        super(id);
    }

    public PolymorphicRecord(String id) {
        super(UUID.fromString(id));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
