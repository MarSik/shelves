package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.api.entities.fields.IsoSizePrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;

@NodeEntity
@DefaultEmberModel(UnitApiModel.class)
public class Unit extends NamedEntity {

    @NotNull
	String symbol;

    @NotNull
	IsoSizePrefix[] prefixes;

    @RelatedTo(type = "OF_UNIT", direction = Direction.INCOMING)
    Iterable<NumericProperty> properties;

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

    @Override
    public boolean canBeDeleted() {
        return !properties.iterator().hasNext();
    }
}
