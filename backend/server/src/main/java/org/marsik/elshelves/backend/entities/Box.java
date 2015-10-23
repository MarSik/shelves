package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DefaultEmberModel(BoxApiModel.class)
public class Box extends NamedEntity
		implements StickerCapable, UpdateableEntity {

	@OneToMany(mappedBy = "parent")
    Set<Box> contains = new THashSet<>();

	public void setContains(Set<Box> update) {
		reconcileLists(update, this::getContains, this::addBox, this::removeBox, true);
	}

	public void addBox(Box b) {
		b.setParent(this);
	}

	public void removeBox(Box b) {
		b.setParent(null);
	}

	@ManyToOne
    Box parent;

	public void setParent(Box b) {
		if (parent != null) parent.getContains().remove(this);
		parent = b;
		if (parent != null) parent.getContains().add(this);
	}

	@OneToMany(mappedBy = "location")
	Set<Lot> lots = new THashSet<>();

	public void addLot(Lot l) {
		l.setLocation(this);
	}

	public void removeLot(Lot l) {
		l.setLocation(null);
	}

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "boxes";
	}

	@Override
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		if (!(update0 instanceof Box)) {
			throw new IllegalArgumentException();
		}

		Box update = (Box)update0;

		update(update.getParent(), this::setParent);
		reconcileLists(update.getContains(), this::getContains, this::addBox, this::removeBox);

		super.updateFrom(update);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkItem(relinker, getParent(), this::setParent);
		relinkList(relinker, this::getContains, this::addBox, this::removeBox);
		relinkList(relinker, this::getLots, this::addLot, this::removeLot);

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
