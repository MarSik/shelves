package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.IsoSizePrefix;

import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("unit")
public class UnitApiModel extends AbstractNamedEntityApiModel {
	String symbol;
	List<IsoSizePrefix> prefixes;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public List<IsoSizePrefix> getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(List<IsoSizePrefix> prefixes) {
		this.prefixes = prefixes;
	}
}
