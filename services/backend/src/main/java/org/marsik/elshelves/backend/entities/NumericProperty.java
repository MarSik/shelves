package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
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

    /**
     * The value is scaled to base units.
     */
    @NotNull
    SiPrefix base;

    String symbol;

    @OneToMany(mappedBy = "property",
            cascade = { CascadeType.ALL })
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
}
