package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.api.entities.fields.FootprintType;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Collection;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
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

	@ManyToMany(mappedBy = "footprints",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Collection<Type> types = new THashSet<>();

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    Set<Footprint> seeAlso = new THashSet<>();

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			mappedBy = "seeAlso")
	Set<Footprint> seeAlsoIncoming = new THashSet<>();

    FootprintType type;

	@Override
	public boolean canBeDeleted() {
		return !getTypes().iterator().hasNext();
	}

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof Footprint)) {
			throw new IllegalArgumentException();
		}

		Footprint update = (Footprint)update0;

		update(update.getKicad(), this::setKicad);
		update(update.getPads(), this::setPads);
		update(update.getHoles(), this::setHoles);
		update(update.getNpth(), this::setNpth);

		update(update.getSeeAlso(), this::setSeeAlso);
		update(update.getType(), this::setType);

		super.updateFrom(update0);
	}
}
