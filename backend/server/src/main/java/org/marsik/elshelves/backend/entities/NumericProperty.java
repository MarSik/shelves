package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DefaultEmberModel(NumericPropertyApiModel.class)
public class NumericProperty extends NamedEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
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
    @Enumerated(EnumType.STRING)
    SiPrefix base;

    String symbol;

    @OneToMany(mappedBy = "property",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true)
    Set<NumericPropertyValue> propertyUses = new THashSet<>();

    @Override
    public boolean canBeDeleted() {
        return !propertyUses.iterator().hasNext();
    }

    @Override
    public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
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

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
