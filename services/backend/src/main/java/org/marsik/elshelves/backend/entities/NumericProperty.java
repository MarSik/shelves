package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.api.entities.fields.IsoSizePrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;

import javax.validation.constraints.NotNull;

@NodeEntity
@DefaultEmberModel(NumericPropertyApiModel.class)
public class NumericProperty extends NamedEntity {
    @PartOfUpdate
    @RelatedTo(type = "OF_UNIT")
    @NotNull
    Unit unit;

    /**
     * The value is scaled to base units.
     */
    @NotNull
    IsoSizePrefix base;

    String symbol;

    @RelatedToVia(type = "HAS_PROPERTY", direction = Direction.INCOMING)
    Iterable<NumericPropertyValue> propertyUses;

    @PartOfUpdate
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @PartOfUpdate
    public IsoSizePrefix getBase() {
        return base;
    }

    public void setBase(IsoSizePrefix base) {
        this.base = base;
    }

    public Iterable<NumericPropertyValue> getPropertyUses() {
        return propertyUses;
    }

    @Override
    public boolean canBeDeleted() {
        return !propertyUses.iterator().hasNext();
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
