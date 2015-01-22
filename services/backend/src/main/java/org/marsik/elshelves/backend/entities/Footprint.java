package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

@NodeEntity
@DefaultEmberModel(FootprintApiModel.class)
public class Footprint extends NamedEntity {
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

	@RelatedTo(type = "HAS_FOOTPRINT", direction = Direction.INCOMING)
	Iterable<Type> types;

	@PartOfUpdate
	public String getKicad() {
		return kicad;
	}

	public void setKicad(String kicad) {
		this.kicad = kicad;
	}

	@PartOfUpdate
	public Integer getPads() {
		return pads;
	}

	public void setPads(Integer pads) {
		this.pads = pads;
	}

	@PartOfUpdate
	public Integer getHoles() {
		return holes;
	}

	public void setHoles(Integer holes) {
		this.holes = holes;
	}

	@PartOfUpdate
	public Integer getNpth() {
		return npth;
	}

	public void setNpth(Integer npth) {
		this.npth = npth;
	}

	public Iterable<Type> getTypes() {
		return types;
	}

	@Override
	public boolean canBeDeleted() {
		return !getTypes().iterator().hasNext();
	}
}
