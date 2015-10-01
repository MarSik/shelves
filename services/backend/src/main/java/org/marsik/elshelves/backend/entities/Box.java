package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(BoxApiModel.class)
public class Box extends NamedEntity
		implements StickerCapable, UpdateableEntity {

	@OneToMany(mappedBy = "parent",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    Set<Box> contains = new THashSet<>();

	@ManyToOne
    Box parent;

	@OneToMany(mappedBy = "location",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Lot> lots = new THashSet<>();

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "boxes";
	}

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof Box)) {
			throw new IllegalArgumentException();
		}

		Box update = (Box)update0;

		updateManyToOne(update.getParent(), this::setParent, this::getParent, Box::getContains, this);
		updateOneToMany(update.getContains(), this::getContains, Box::setParent, this);

		super.updateFrom(update);
	}
}
