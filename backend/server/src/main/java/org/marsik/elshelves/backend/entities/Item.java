package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.UuidGenerator;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@DefaultEmberModel(ItemApiModel.class)
@Entity
public class Item extends Lot implements StickerCapable {
	@OneToMany(mappedBy = "item",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	Set<Requirement> requires = new THashSet<>();

	public void addRequirement(Requirement r) {
		r.setItem(this);
	}

	public void removeRequirement(Requirement r) {
		r.setItem(null);
	}

	Boolean finished = false;

	public boolean canBeDeleted() {
		for (Requirement r: getRequires()) {
			if (!r.canBeDeleted()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "items";
	}

	@Override
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		super.updateFrom(update0);

		/* Item can be updated using a Lot instance too. Skip Item fields
	       in that case.
	    */
		if (!(update0 instanceof Item)) {
			return;
		}

		Item update = (Item)update0;

		update(update.getFinished(), this::setFinished);
		reconcileLists(update.getRequires(), this::getRequires, this::addRequirement, this::removeRequirement);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkList(relinker, this::getRequires, this::addRequirement, this::removeRequirement);
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

	@Override
	public boolean isRevisionNeeded(UpdateableEntity update) {
		if (super.isRevisionNeeded(update)) {
			return true;
		}

		/* Item can be updated using a Lot instance too. Skip Item fields
	       in that case.
	    */
		if (!(update instanceof Item)) {
			return false;
		}

		return willUpdate(getFinished(), ((Item) update).getFinished());
	}
}
