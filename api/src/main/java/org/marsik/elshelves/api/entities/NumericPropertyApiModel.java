package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.IsoSizePrefix;
import org.marsik.elshelves.api.entities.idresolvers.IsoSizePrefixDeserializer;
import org.marsik.elshelves.api.entities.idresolvers.NumericPropertyIdResolver;

@EmberModelName("property")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = NumericPropertyIdResolver.class)
public class NumericPropertyApiModel extends AbstractNamedEntityApiModel {
    UnitApiModel unit;
    IsoSizePrefix base;

    @JsonIdentityReference(alwaysAsId = true)
    public UnitApiModel getUnit() {
        return unit;
    }

    public void setUnit(UnitApiModel unit) {
        this.unit = unit;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public IsoSizePrefix getBase() {
        return base;
    }

    @JsonDeserialize(using = IsoSizePrefixDeserializer.class)
    public void setBase(IsoSizePrefix base) {
        this.base = base;
    }
}
