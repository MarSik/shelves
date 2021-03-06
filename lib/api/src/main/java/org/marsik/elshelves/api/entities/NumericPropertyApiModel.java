package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.api.entities.idresolvers.NumericPropertyIdResolver;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@JsonTypeName("property")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = NumericPropertyIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = NumericPropertyApiModel.class)
public class NumericPropertyApiModel extends AbstractNamedEntityApiModel {
    public NumericPropertyApiModel(UUID id) {
        super(id);
    }

    public NumericPropertyApiModel() {
    }

    public NumericPropertyApiModel(String uuid) {
        super(uuid);
    }

    String symbol;

    @NotNull
    UnitApiModel unit;

    @NotNull
    SiPrefix base;

    @JsonSerialize(using = ToStringSerializer.class)
    public SiPrefix getBase() {
        return base;
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
