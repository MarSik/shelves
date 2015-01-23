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
import org.marsik.elshelves.api.entities.idresolvers.UnitIdResolver;

import java.util.List;
import java.util.UUID;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = UnitIdResolver.class)
@EmberModelName("unit")
public class UnitApiModel extends AbstractNamedEntityApiModel {
    public UnitApiModel(UUID id) {
        super(id);
    }

    public UnitApiModel() {
    }

    String symbol;
	List<IsoSizePrefix> prefixes;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

    @JsonSerialize(contentUsing = ToStringSerializer.class)
	public List<IsoSizePrefix> getPrefixes() {
		return prefixes;
	}

    @JsonDeserialize(contentUsing = IsoSizePrefixDeserializer.class)
	public void setPrefixes(List<IsoSizePrefix> prefixes) {
		this.prefixes = prefixes;
	}
}
