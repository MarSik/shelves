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
import org.marsik.elshelves.api.entities.idresolvers.UnitIdResolver;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = UnitIdResolver.class)
@JsonTypeName("unit")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = UnitApiModel.class)
public class UnitApiModel extends AbstractNamedEntityApiModel {
    public UnitApiModel(UUID id) {
        super(id);
    }

    public UnitApiModel() {
    }

    public UnitApiModel(String uuid) {
        super(uuid);
    }

    String symbol;
	Set<SiPrefix> prefixes;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
	public Set<SiPrefix> getPrefixes() {
		return prefixes;
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
