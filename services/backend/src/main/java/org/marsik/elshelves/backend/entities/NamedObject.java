package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;
import java.util.UUID;

@NodeEntity
public class NamedObject implements OwnedEntity {
	@RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
	User owner;

	@Indexed
	UUID uuid;

	@Indexed
	String name;

	@RelatedTo(type = "DESCRIBES", direction = Direction.INCOMING)
	Set<Document> describedBy;

	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Document> getDescribedBy() {
		return describedBy;
	}

	public void setDescribedBy(Set<Document> describedBy) {
		this.describedBy = describedBy;
	}
}
