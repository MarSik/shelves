package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@NodeEntity
public abstract class OwnedEntity {
	@PartOfUpdate
	@RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
	@NotNull
	User owner;

	@NotNull
	@Indexed
	UUID uuid;

	// Provided by AspectJ-ized NodeEntity
	// public abstract Long getNodeId();

	@PartOfUpdate
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public abstract boolean canBeDeleted();
}
