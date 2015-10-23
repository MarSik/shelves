package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.interfaces.Relinker;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.function.Predicate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Requirement extends IdentifiedEntity implements OwnedEntityInterface, UpdateableEntity {
	@ManyToOne(optional = false)
	@NotNull
	Item item;

	public void setItem(Item i) {
		if (item != null) item.getRequires().remove(this);
		item = i;
		if (item != null) item.getRequires().add(this);
	}

	public void unsetItem(Item i) {
		assert i.equals(item);
		setItem(null);
	}

	@ManyToMany
	Set<Type> type = new THashSet<>();

	public void addType(Type t) {
		type.add(t);
		t.getUsedIn().add(this);
	}

	public void removeType(Type t) {
		type.remove(t);
		t.getUsedIn().remove(this);
	}

	@OneToMany(mappedBy = "usedBy")
	Set<Lot> lots = new THashSet<>();

	public void addLot(Lot l) {
		l.setUsedBy(this);
	}

	public void removeLot(Lot l) {
		l.setUsedBy(null);
	}

    String name;
    String summary;

	@Min(1)
	Long count;

	@Override
	public User getOwner() {
		return getItem() != null ? getItem().getOwner() : null;
	}

	@Override
	public void setOwner(User user) {
		// NOP
	}

	public boolean canBeDeleted() {
		return getLots().stream().allMatch(new Predicate<Lot>() {
			@Override
			public boolean test(Lot lot) {
				return !lot.getStatus().equals(LotAction.SOLDERED);
			}
		});
	}

	@Override
	public boolean canBeUpdated() {
		return false;
	}

	@Override
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		if (!(update0 instanceof Requirement)) {
			throw new IllegalArgumentException();
		}

		Requirement update = (Requirement)update0;

		update(update.getName(), this::setName);
		update(update.getSummary(), this::setSummary);
		update(update.getCount(), this::setCount);

		reconcileLists(update.getType(), this::getType, this::addType, this::removeType);
		reconcileLists(update.getLots(), this::getLots, this::addLot, this::removeLot);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkList(relinker, this::getType, this::addType, this::removeType);
		relinkItem(relinker, getItem(), this::setItem);
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
