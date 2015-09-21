package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
public class Requirement extends OwnedEntity {
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Project project;

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
	public Project getProject() {
		return project;
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
		return getProject() != null ? getProject().getOwner() : null;
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
        for (Lot l: getRawLots()) {
            if (l.isValid()) {
                assigned.add(l);
            }
        }

        return assigned;
    }

	public boolean canBeDeleted() {
		return getLots().isEmpty();
	}
}
