package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.fields.LotAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import java.util.Set;
import java.util.function.Predicate;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public class Requirement extends OwnedEntity {
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Item item;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Type> type;

	@OneToMany(mappedBy = "usedBy",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Lot> rawLots;

    String name;
    String summary;

	@Min(1)
	Long count;

	@PartOfUpdate
	public Item getItem() {
		return item;
	}

	@PartOfUpdate
	public Set<Type> getType() {
		return type;
	}

	@PartOfUpdate
	public Long getCount() {
		return count;
	}

	@Override
	public User getOwner() {
		return getItem() != null ? getItem().getOwner() : null;
	}

	@Override
	public void setOwner(User user) {
		// NOP
	}

    @PartOfUpdate
    public String getName() {
        return name;
    }

    @PartOfUpdate
    public String getSummary() {
        return summary;
    }

    /**
     * Return all active lots that are assigned to this
     * requirement.
     */
    public Set<Lot> getLots() {
        Set<Lot> assigned = new THashSet<>();
		if (getRawLots() == null) {
			return assigned;
		}

        for (Lot l: getRawLots()) {
            if (l.isValid()) {
                assigned.add(l);
            }
        }

        return assigned;
    }

	public boolean canBeDeleted() {
		return getLots().stream().allMatch(new Predicate<Lot>() {
			@Override
			public boolean test(Lot lot) {
				return !lot.getStatus().equals(LotAction.SOLDERED);
			}
		});
	}

	@Override
	public boolean canBeUpdated() {
		return false;
	}
}
