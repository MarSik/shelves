package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.ItemApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@DefaultEmberModel(ItemApiModel.class)
@Entity
public class Item extends Lot implements StickerCapable {
	@OneToMany(mappedBy = "item",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE },
			orphanRemoval = true)
	Set<Requirement> requires = new THashSet<>();

	public void addRequirement(Requirement r) {
		r.setItem(this);
	}

	public void removeRequirement(Requirement r) {
		r.setItem(null);
	}

	Boolean finished;

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
		reconcileLists(this, update, Item::getRequires, Item::addRequirement, Item::removeRequirement);

		super.updateFrom(update0);
	}
}
