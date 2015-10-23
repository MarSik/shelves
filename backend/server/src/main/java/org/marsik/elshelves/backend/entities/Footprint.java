package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.api.entities.FootprintApiModel;
import org.marsik.elshelves.api.entities.fields.FootprintType;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
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
	Set<Type> types = new THashSet<>();

	public void addType(Type t) {
		t.addFootprint(this);
	}

	public void removeType(Type t) {
		t.removeFootprint(this);
	}

	@ManyToMany
    Set<Footprint> seeAlso = new THashSet<>();

	@ManyToMany(mappedBy = "seeAlso")
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
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		if (!(update0 instanceof Footprint)) {
			throw new IllegalArgumentException();
		}

		Footprint update = (Footprint)update0;

		update(update.getKicad(), this::setKicad);
		update(update.getPads(), this::setPads);
		update(update.getHoles(), this::setHoles);
		update(update.getNpth(), this::setNpth);
		update(update.getType(), this::setType);

		reconcileLists(update.getTypes(), this::getTypes, this::addType, this::removeType);
		reconcileLists(update.getSeeAlso(), this::getSeeAlso, this::addSeeAlso, this::removeSeeAlso);

		super.updateFrom(update0);
	}

	@Override
	public void relink(Relinker relinker) {

		relinkList(relinker, this::getTypes, this::addType, this::removeType);
		relinkList(relinker, this::getSeeAlso, this::addSeeAlso, this::removeSeeAlso);

		super.relink(relinker);
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
