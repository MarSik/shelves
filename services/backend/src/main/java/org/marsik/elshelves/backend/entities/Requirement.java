package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@NodeEntity
public class Requirement extends OwnedEntity {
	@NotNull
	@RelatedTo(type = "REQUIRES", direction = Direction.INCOMING)
	Project project;

	@NotNull
	@RelatedTo(type = "REQUIRED_TYPE")
	Set<Type> type;

	@RelatedTo(type = "USES")
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
