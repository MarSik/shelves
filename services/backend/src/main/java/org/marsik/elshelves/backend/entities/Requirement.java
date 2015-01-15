package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

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

	@Min(1)
	Long count;

	@PartOfUpdate
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@PartOfUpdate
	public Set<Type> getType() {
		return type;
	}

	public void setType(Set<Type> type) {
		this.type = type;
	}

	@PartOfUpdate
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public User getOwner() {
		return getProject() != null ? getProject().getOwner() : null;
	}

	@Override
	public void setOwner(User user) {
		// NOP
	}

	public Set<Lot> getRawLots() {
		return rawLots;
	}

	public void setRawLots(Set<Lot> lots) {
		this.rawLots = lots;
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
