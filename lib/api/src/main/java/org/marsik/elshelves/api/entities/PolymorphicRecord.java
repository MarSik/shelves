package org.marsik.elshelves.api.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.ember.EmberModel;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
public class PolymorphicRecord extends AbstractEntityApiModel {
    String type;

    public static PolymorphicRecord build(AbstractEntityApiModel entity) {
        PolymorphicRecord p = new PolymorphicRecord();
        p.setType(EmberModel.getSingularName(entity.getClass()));
        p.setId(entity.getId());
        return p;
    }
}
