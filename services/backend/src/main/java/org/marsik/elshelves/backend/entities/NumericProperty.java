package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;
import org.springframework.data.neo4j.annotation.RelatedToVia;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
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
    SiPrefix base;

    String symbol;

    @RelatedToVia(type = "HAS_PROPERTY", direction = Direction.INCOMING)
    Iterable<NumericPropertyValue> propertyUses;

    @PartOfUpdate
    public Unit getUnit() {
        return unit;
    }

    @PartOfUpdate
    public SiPrefix getBase() {
        return base;
    }

    @Override
    public boolean canBeDeleted() {
        return !propertyUses.iterator().hasNext();
    }

    @PartOfUpdate
    public String getSymbol() {
        return symbol;
    }
}
