package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
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
            cascade = { CascadeType.ALL })
    Collection<NumericProperty> unitUses = new THashSet<>();

    @PartOfUpdate
	public String getSymbol() {
		return symbol;
	}

    @PartOfUpdate
	public Set<SiPrefix> getPrefixes() {
		return prefixes;
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
}
