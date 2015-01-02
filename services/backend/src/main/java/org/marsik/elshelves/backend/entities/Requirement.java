package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@NodeEntity
public class Requirement implements OwnedEntity {
	@Indexed
	UUID uuid;

	@NotNull
	@RelatedTo(type = "REQUIRES", direction = Direction.INCOMING)
	Project project;

	@NotNull
	@RelatedTo(type = "REQUIRED_TYPE")
	Type type;

	@Min(1)
	Long count;

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
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
		return getProject().getOwner();
	}

	@Override
	public void setOwner(User user) {
		// NOP
	}
}
