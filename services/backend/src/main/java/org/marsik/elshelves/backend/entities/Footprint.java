package org.marsik.elshelves.backend.entities;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@NodeEntity
public class Footprint extends NamedObject {
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
