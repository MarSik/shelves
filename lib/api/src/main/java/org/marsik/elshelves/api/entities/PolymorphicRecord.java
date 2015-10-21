package org.marsik.elshelves.api.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModel;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PolymorphicRecord extends AbstractEntityApiModel {
    String type;

    public PolymorphicRecord(UUID uuid) {
        super(uuid);
    }

    public PolymorphicRecord(String uuid) {
        super(UUID.fromString(uuid));
    }

    public static PolymorphicRecord build(AbstractEntityApiModel entity) {
        PolymorphicRecord p = new PolymorphicRecord();
        p.setType(EmberModel.getSingularName(entity.getClass()));
        p.setId(entity.getId());
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
