package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;
import java.util.UUID;

@NodeEntity
public class Footprint implements OwnedEntity {
	@Indexed
	UUID uuid;

	@RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
	User owner;

	String name;
	String kicad;

	/**
	 * Number of solderable connections
	 */
	Integer pads;

	/**
	 * Number of plated holes
	 */
	Integer holes;

	/**
	 * Number of non-plated holes
	 */
	Integer npth;


	@RelatedTo(type = "DESCRIBES", direction = Direction.INCOMING)
	Set<Document> describedBy;

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

	public String getKicad() {
		return kicad;
	}

	public void setKicad(String kicad) {
		this.kicad = kicad;
	}

	public Integer getPads() {
		return pads;
	}

	public void setPads(Integer pads) {
		this.pads = pads;
	}

	public Integer getHoles() {
		return holes;
	}

	public void setHoles(Integer holes) {
		this.holes = holes;
	}

	public Integer getNpth() {
		return npth;
	}

	public void setNpth(Integer npth) {
		this.npth = npth;
	}

	public Set<Document> getDescribedBy() {
		return describedBy;
	}

	public void setDescribedBy(Set<Document> describedBy) {
		this.describedBy = describedBy;
	}
}
