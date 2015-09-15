package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.api.entities.fields.FootprintType;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
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

	@ManyToMany(mappedBy = "footprints")
	Iterable<Type> types;

	@ManyToMany(mappedBy = "seeAlso")
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
