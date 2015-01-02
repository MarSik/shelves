package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;
import java.util.UUID;

@NodeEntity
public class Type extends NamedObject {
	String description;

	String vendor;
	String vendorId;

	@RelatedTo(type = "HAS_FOOTPRINT")
	Footprint footprint;

	@RelatedTo(type = "CONTAINS", direction = Direction.INCOMING)
	Set<Group> groups;

	@RelatedTo(type = "OF_TYPE", direction = Direction.INCOMING)
	Set<Lot> lots;

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

	public Set<Lot> getLots() {
		return lots;
	}

	public void setLots(Set<Lot> lots) {
		this.lots = lots;
	}
}
