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
    Unit unit;

    /**
     * The value is scaled to base units.
     */
    @NotNull
    IsoSizePrefix base;

    @RelatedToVia(type = "HAS_PROPERTY", direction = Direction.INCOMING)
    Iterable<NumericPropertyValue> propertyUses;

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public IsoSizePrefix getBase() {
        return base;
    }

    public void setBase(IsoSizePrefix base) {
        this.base = base;
    }

    public Iterable<NumericPropertyValue> getPropertyUses() {
        return propertyUses;
    }

    public void setPropertyUses(Iterable<NumericPropertyValue> propertyUses) {
        this.propertyUses = propertyUses;
    }

    @Override
    public boolean canBeDeleted() {
        return !propertyUses.iterator().hasNext();
    }
}
