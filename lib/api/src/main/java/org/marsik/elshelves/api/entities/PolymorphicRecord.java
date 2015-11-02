package org.marsik.elshelves.api.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelHelper;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PolymorphicRecord extends AbstractEntityApiModel {
    String type;

    public PolymorphicRecord(String uuid) {
        super(UUID.fromString(uuid));
    }

    public static PolymorphicRecord build(AbstractEntityApiModel entity) {
        PolymorphicRecord p = new PolymorphicRecord();
        p.setType(EmberModelHelper.getSingularName(entity.getClass()));
        p.setId(entity.getId());
        return p;
    }

    public static PolymorphicRecord build(String type, UUID id) {
        PolymorphicRecord p = new PolymorphicRecord();
        p.setType(type);
        p.setId(id);
        return p;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
