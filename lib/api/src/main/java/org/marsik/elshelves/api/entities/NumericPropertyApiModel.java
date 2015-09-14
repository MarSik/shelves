package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.api.entities.idresolvers.NumericPropertyIdResolver;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
@EmberModelName("property")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = NumericPropertyIdResolver.class)
public class NumericPropertyApiModel extends AbstractNamedEntityApiModel {
    public NumericPropertyApiModel(UUID id) {
        super(id);
    }

    public NumericPropertyApiModel() {
    }

    String symbol;

    @NotNull
    UnitApiModel unit;

    @NotNull
    SiPrefix base;

    @JsonIdentityReference(alwaysAsId = true)
    public UnitApiModel getUnit() {
        return unit;
    }

    @JsonSerialize(using = ToStringSerializer.class)
    public SiPrefix getBase() {
        return base;
    }
}
