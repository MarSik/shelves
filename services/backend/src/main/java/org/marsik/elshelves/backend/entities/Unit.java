package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.api.entities.fields.IsoSizePrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
@DefaultEmberModel(UnitApiModel.class)
public class Unit extends NamedEntity {

	String symbol;
	IsoSizePrefix[] prefixes;

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	public String getSymbol() {
		return symbol;
	}

	@PartOfUpdate
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public IsoSizePrefix[] getPrefixes() {
		return prefixes;
	}

	@PartOfUpdate
	public void setPrefixes(IsoSizePrefix[] prefixes) {
		this.prefixes = prefixes;
	}
}
