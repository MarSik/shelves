package org.marsik.elshelves.backend.entities;

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
	Set<Lot> lots;

	@Min(1)
	Long count;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Set<Type> getType() {
		return type;
	}

	public void setType(Set<Type> type) {
		this.type = type;
	}

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

	public Set<Lot> getLots() {
		return lots;
	}

	public void setLots(Set<Lot> lots) {
		this.lots = lots;
	}
}
