package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.UuidGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@DefaultEmberModel(ItemApiModel.class)
@Entity
public class Item extends Lot implements StickerCapable {
	@OneToMany(mappedBy = "item",
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
		return "projects";
	}

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof Item)) {
			throw new IllegalArgumentException();
		}

		Item update = (Item)update0;

		update(update.getFinished(), this::setFinished);
		reconcileLists(update.getRequires(), this::getRequires, this::addRequirement, this::removeRequirement);

		super.updateFrom(update0);
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

	public LotHistory finishOrReopen(boolean finish, User performedBy, UuidGenerator uuidGenerator) {
		if (getFinished() != null
				&& getFinished() == finish) {
			return getHistory();
		}

		setFinished(finish);
		return recordChange(finish ? LotAction.FINISHED : LotAction.REOPENED, performedBy, uuidGenerator);
	}
}