package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
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
	SiPrefix[] prefixes;

    @RelatedTo(type = "OF_UNIT", direction = Direction.INCOMING)
    Iterable<NumericProperty> unitUses;

    @PartOfUpdate
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

    @PartOfUpdate
	public SiPrefix[] getPrefixes() {
		return prefixes;
	}

	public void setPrefixes(SiPrefix[] prefixes) {
		this.prefixes = prefixes;
	}

    public Iterable<NumericProperty> getUnitUses() {
        return unitUses;
    }

    @Override
    public boolean canBeDeleted() {
        return !getUnitUses().iterator().hasNext();
    }
}
