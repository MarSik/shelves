package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.BoxApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(BoxApiModel.class)
public class Box extends NamedEntity implements StickerCapable {

	@OneToMany(mappedBy = "parent",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    Set<Box> contains;

	@ManyToOne
    Box parent;

	@OneToMany(mappedBy = "location",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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
