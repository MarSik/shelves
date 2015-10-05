package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DefaultEmberModel(UnitApiModel.class)
public class Unit extends NamedEntity {

    @NotNull
	String symbol;

    @NotNull
    @ElementCollection(targetClass = SiPrefix.class)
    @Enumerated(EnumType.STRING)
    Set<SiPrefix> prefixes = new THashSet<>();

    @OneToMany(mappedBy = "unit",
            cascade = { CascadeType.ALL },
            orphanRemoval = true)
    Set<NumericProperty> unitUses = new THashSet<>();

    public void addUse(NumericProperty p) {
        p.setUnit(this);
    }

    public void removeUse(NumericProperty p) {
        p.unsetUnit(this);
    }

    @Override
    public boolean canBeDeleted() {
        return getUnitUses() == null || !getUnitUses().iterator().hasNext();
    }

    @Override
    public void updateFrom(UpdateableEntity update0) {
        if (!(update0 instanceof Unit)) {
            throw new IllegalArgumentException();
        }

        Unit update = (Unit)update0;

        update(update.getSymbol(), this::setSymbol);
        update(update.getPrefixes(), this::setPrefixes);

        super.updateFrom(update0);
    }

    @Override
    public void relink(Relinker relinker) {
        relinkList(relinker, this::getUnitUses, this::addUse, this::removeUse);
        super.relink(relinker);
    }
}
