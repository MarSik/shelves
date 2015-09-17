package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.marsik.elshelves.api.entities.UnitApiModel;
import org.marsik.elshelves.api.entities.fields.SiPrefix;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(UnitApiModel.class)
public class Unit extends NamedEntity {

    @NotNull
	String symbol;

    @NotNull
    @ElementCollection(targetClass = SiPrefix.class)
    Set<SiPrefix> prefixes;

    @OneToMany(mappedBy = "unit")
    Collection<NumericProperty> unitUses;

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
        return !getUnitUses().iterator().hasNext();
    }
}
