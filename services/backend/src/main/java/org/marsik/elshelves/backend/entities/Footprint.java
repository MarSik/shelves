package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.api.entities.fields.FootprintType;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
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

    @RelatedTo(type = "SEE_ALSO_FP", direction = Direction.BOTH)
    Set<Footprint> seeAlso;

    FootprintType type;

	@PartOfUpdate
	public String getKicad() {
		return kicad;
	}

	@PartOfUpdate
	public Integer getPads() {
		return pads;
	}

	@PartOfUpdate
	public Integer getHoles() {
		return holes;
	}

	@PartOfUpdate
	public Integer getNpth() {
		return npth;
	}

	@Override
	public boolean canBeDeleted() {
		return !getTypes().iterator().hasNext();
	}

    @PartOfUpdate
    public Set<Footprint> getSeeAlso() {
        return seeAlso;
    }

    @PartOfUpdate
    public FootprintType getType() {
        return type;
    }
}
