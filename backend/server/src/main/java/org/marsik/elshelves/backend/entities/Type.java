package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.entities.fields.PartCount;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@ToString(of = {}, callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@Entity
@DefaultEmberModel(PartTypeApiModel.class)
public class Type extends NamedEntity implements StickerCapable {
	String vendor;
	String customId;

    Long minimumCount;
    Long buyMultiple;

    // Should serial numbers be tracked?
    Boolean serials = false;

	/**
	 * Enable manufacturability of this type, When this is set to true
	 * it will allow tracking of separate items with requirements, sub-components'
	 * placement and solder status and so no
	 */
	Boolean manufacturable = false;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Footprint> footprints = new THashSet<>();

	public void addFootprint(Footprint fp) {
		footprints.add(fp);
		fp.getTypes().add(this);
	}

	public void removeFootprint(Footprint fp) {
		footprints.remove(fp);
		fp.getTypes().remove(this);
	}

	@ManyToMany(mappedBy = "types",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Group> groups = new THashSet<>();

	public void addGroup(Group g) {
		g.addType(this);
	}

	public void removeGroup(Group g) {
		g.removeType(this);
	}

	@OneToMany(mappedBy = "type",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Purchase> purchases = new THashSet<>();

	public void addPurchase(Purchase p) {
		p.setType(this);
	}

	public void removePurchase(Purchase p) {
		p.unsetType(this);
	}

	@ManyToMany(mappedBy = "type",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Set<Requirement> usedIn = new THashSet<>();

	public void addUsedIn(Requirement r) {
		r.addType(this);
	}

	public void removeUsedIn(Requirement r) {
		r.removeType(this);
	}

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    Set<Type> seeAlso = new THashSet<>();

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			mappedBy = "seeAlso")
	Set<Type> seeAlsoIncoming = new THashSet<>();

	public void addSeeAlso(Type t) {
		seeAlso.add(t);
		seeAlsoIncoming.add(t);
		t.getSeeAlso().add(t);
		t.getSeeAlsoIncoming().add(t);
	}

	public void removeSeeAlso(Type t) {
		seeAlso.remove(t);
		seeAlsoIncoming.remove(t);
		t.getSeeAlso().remove(t);
		t.getSeeAlsoIncoming().remove(t);
	}

	public Iterable<Lot> getLots() {
		List<Lot> lots = new ArrayList<>();

		if (getPurchases() == null) {
			return lots;
		}

		for (Purchase p: getPurchases()) {
			for (Lot l: p.getLots()) {
				lots.add(l);
			}
		}

		return lots;
	}

	@Override
	public boolean canBeDeleted() {
		return !(getPurchases().iterator().hasNext() || getUsedIn().iterator().hasNext());
	}

    public PartCount getAvailable() {
        PartCount count = new PartCount();
        for (Lot l: getLots()) {
            if (l.isCanBeAssigned()) {
                count.available += l.getCount();
                count.free += l.getCount();
                count.total += l.getCount();
            } else if (l.isCanBeSoldered()) {
                count.available += l.getCount();
                count.total += l.getCount();
            } else if (l.isCanBeUnsoldered()) {
                count.total += l.getCount();
            }
        }

        return count;
    }

	@Override
	public String getBaseUrl() {
		return "types";
	}

	@Override
	public void updateFrom(UpdateableEntity update0) {
		if (!(update0 instanceof Type)) {
			throw new IllegalArgumentException();
		}

		Type update = (Type)update0;

		update(update.getVendor(), this::setVendor);
		update(update.getCustomId(), this::setCustomId);

		update(update.getSerials(), this::setSerials);
		update(update.getMinimumCount(), this::setMinimumCount);
		update(update.getBuyMultiple(), this::setBuyMultiple);
		update(update.getManufacturable(), this::setManufacturable);

		reconcileLists(this, update, Type::getSeeAlso, Type::addSeeAlso, Type::removeSeeAlso);
		reconcileLists(this, update, Type::getFootprints, Type::addFootprint, Type::removeFootprint);
		reconcileLists(this, update, Type::getGroups, Type::addGroup, Type::removeGroup);

		super.updateFrom(update0);
	}
}
