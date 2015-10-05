package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(NumericPropertyApiModel.class)
public class NumericProperty extends NamedEntity {
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
            optional = false)
    @NotNull
    Unit unit;

    public void setUnit(Unit u) {
        if (unit != null) unit.getUnitUses().remove(this);
        unit = u;
        if (unit != null) unit.getUnitUses().add(this);
    }

    public void unsetUnit(Unit u) {
        assert u.equals(unit);
        setUnit(null);
    }

    /**
     * The value is scaled to base units.
     */
    @NotNull
    SiPrefix base;

    String symbol;

    @OneToMany(mappedBy = "property",
            cascade = { CascadeType.ALL },
            orphanRemoval = true)
    Collection<NumericPropertyValue> propertyUses = new THashSet<>();

    @Override
    public boolean canBeDeleted() {
        return !propertyUses.iterator().hasNext();
    }

    @Override
    public void updateFrom(UpdateableEntity update0) {
        if (!(update0 instanceof NumericProperty)) {
            throw new IllegalArgumentException();
        }

        NumericProperty update = (NumericProperty)update0;

        update(update.getUnit(), this::setUnit);
        update(update.getBase(), this::setBase);
        update(update.getSymbol(), this::setSymbol);

        super.updateFrom(update0);
    }

    @Override
    public void relink(Relinker relinker) {
        relinkItem(relinker, getUnit(), this::setUnit);

        // Use a copy of the collection to prevent concurrent modification exception
        for (NumericPropertyValue value: new ArrayList<>(getPropertyUses())) {
            value.relink(relinker);
        }

        super.relink(relinker);
    }
}
