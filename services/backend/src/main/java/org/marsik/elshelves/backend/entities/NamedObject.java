package org.marsik.elshelves.backend.entities;

import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@NodeEntity
public class NamedObject implements OwnedEntity {
	@RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
	@NotNull
	User owner;

	@NotNull
	@Indexed
	UUID uuid;

	@Indexed
	@NotEmpty
	@NotNull
	String name;

	@Indexed
	String summary;

	@Indexed
	String description;

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

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Document> getDescribedBy() {
		return describedBy;
	}

	public void setDescribedBy(Set<Document> describedBy) {
		this.describedBy = describedBy;
	}
}
