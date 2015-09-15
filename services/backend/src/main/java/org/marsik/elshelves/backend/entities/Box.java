package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(BoxApiModel.class)
public class Box extends NamedEntity implements StickerCapable {

	@OneToMany(mappedBy = "parent")
    Set<Box> contains;

	@ManyToOne
    Box parent;

	@OneToMany(mappedBy = "location")
	Set<Lot> lots;

	@PartOfUpdate
    public Box getParent() {
        return parent;
    }

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	@Override
	public String getBaseUrl() {
		return "boxes";
	}
}
