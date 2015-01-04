package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@NodeEntity
public class Type extends NamedObject {
	String vendor;
	String vendorId;

	@RelatedTo(type = "HAS_FOOTPRINT")
	Footprint footprint;

	@RelatedTo(type = "CONTAINS", direction = Direction.INCOMING)
	Set<Group> groups;

	@RelatedTo(type = "OF_TYPE", direction = Direction.INCOMING)
	Set<Purchase> purchases;

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

	public Iterable<Lot> getLots() {
		List<Lot> lots = new ArrayList<>();
		for (Purchase p: getPurchases()) {
			for (Lot l: p.getLots()) {
				lots.add(l);
			}
		}

		return lots;
	}

	public Set<Purchase> getPurchases() {
		return purchases;
	}

	public void setPurchases(Set<Purchase> purchases) {
		this.purchases = purchases;
	}
}
