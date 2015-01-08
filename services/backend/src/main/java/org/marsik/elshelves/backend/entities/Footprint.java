package org.marsik.elshelves.backend.entities;

import org.springframework.data.neo4j.annotation.NodeEntity;

@NodeEntity
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
}
