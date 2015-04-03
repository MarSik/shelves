package org.marsik.elshelves.api.entities;

import org.marsik.elshelves.api.ember.EmberModel;

import java.util.UUID;

public class PolymorphicRecord extends AbstractEntityApiModel {
    String type;

    public PolymorphicRecord() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static PolymorphicRecord build(AbstractEntityApiModel entity) {
        PolymorphicRecord p = new PolymorphicRecord();
        p.setType(EmberModel.getSingularName(entity.getClass()));
        p.setId(entity.getId());
        return p;
    }
}
