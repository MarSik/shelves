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

	public void addType(Type t) {
		t.addFootprint(this);
	}

	public void removeType(Type t) {
		t.removeFootprint(this);
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    Set<Footprint> seeAlso = new THashSet<>();

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			mappedBy = "seeAlso")
	Set<Footprint> seeAlsoIncoming = new THashSet<>();

	public void addSeeAlso(Footprint fp) {
		seeAlso.add(fp);
		seeAlsoIncoming.add(fp);
		fp.getSeeAlso().add(fp);
		fp.getSeeAlsoIncoming().add(fp);
	}

	public void removeSeeAlso(Footprint fp) {
		seeAlso.remove(fp);
		seeAlsoIncoming.remove(fp);
		fp.getSeeAlso().remove(fp);
		fp.getSeeAlsoIncoming().remove(fp);
	}


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
		update(update.getType(), this::setType);

		reconcileLists(this, update, Footprint::getTypes, Type::addFootprint, Type::removeFootprint);

		super.updateFrom(update0);
	}
}
