package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.NumericPropertyApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
    @PartOfUpdate
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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
    Collection<NumericPropertyValue> propertyUses;

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
