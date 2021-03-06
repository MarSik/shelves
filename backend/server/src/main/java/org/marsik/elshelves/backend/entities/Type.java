package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.api.entities.PartTypeApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.entities.fields.PartCount;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.StickerCapable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
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

	@OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
	Set<Sku> skus = new THashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	Set<Footprint> footprints = new THashSet<>();

	public void addFootprint(Footprint fp) {
		footprints.add(fp);
		fp.getTypes().add(this);
	}

	public void removeFootprint(Footprint fp) {
		footprints.remove(fp);
		fp.getTypes().remove(this);
	}

	@ManyToMany(mappedBy = "types", fetch = FetchType.LAZY)
	Set<Group> groups = new THashSet<>();

	public void addGroup(Group g) {
		g.addType(this);
	}

	public void removeGroup(Group g) {
		g.removeType(this);
	}

	@OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
	Set<Purchase> purchases = new THashSet<>();

	public void addPurchase(Purchase p) {
		p.setType(this);
	}

	public void removePurchase(Purchase p) {
		p.unsetType(this);
	}

	@ManyToMany(mappedBy = "type", fetch = FetchType.LAZY)
	Set<Requirement> usedIn = new THashSet<>();

	public void addUsedIn(Requirement r) {
		r.addType(this);
	}

	public void removeUsedIn(Requirement r) {
		r.removeType(this);
	}

	@ManyToMany(fetch = FetchType.LAZY)
    Set<Type> seeAlso = new THashSet<>();

	@ManyToMany(mappedBy = "seeAlso", fetch = FetchType.LAZY)
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "type")
	Set<Lot> lots = new THashSet<>();

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
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
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

		reconcileLists(update.getSeeAlso(), this::getSeeAlso, this::addSeeAlso, this::removeSeeAlso);
		reconcileLists(update.getFootprints(), this::getFootprints, this::addFootprint, this::removeFootprint);
		reconcileLists(update.getGroups(), this::getGroups, this::addGroup, this::removeGroup);

		super.updateFrom(update0);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkList(relinker, this::getFootprints, this::addFootprint, this::removeFootprint);
		relinkList(relinker, this::getGroups, this::addGroup, this::removeGroup);
		relinkList(relinker, this::getPurchases, this::addPurchase, this::removePurchase);
		relinkList(relinker, this::getUsedIn, this::addUsedIn, this::removeUsedIn);
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

	public void addLot(Lot lot) {
		lot.setType(this);
	}
}
