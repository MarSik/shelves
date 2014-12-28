package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;
import java.util.UUID;

@NodeEntity
public class Type implements OwnedEntity {
	@Indexed
	UUID uuid;

	@RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
	User owner;

	String name;
	String description;

	String vendor;
	String vendorId;

	@RelatedTo(type = "HAS_FOOTPRINT")
	Footprint footprint;

	@RelatedTo(type = "CONTAINS", direction = Direction.INCOMING)
	Set<Group> groups;

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public Footprint getFootprint() {
		return footprint;
	}

	public void setFootprint(Footprint footprint) {
		this.footprint = footprint;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}
}