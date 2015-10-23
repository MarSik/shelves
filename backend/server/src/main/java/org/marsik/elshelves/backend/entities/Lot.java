package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.fields.DefaultEmberModel;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.UuidGenerator;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DefaultEmberModel(LotApiModel.class)
public class Lot extends OwnedEntity implements StickerCapable {
	public Lot(UUID uuid) {
		setId(uuid);
	}

	@NotNull
	@Min(1)
	Long count;

	@NotNull
	@Enumerated(EnumType.STRING)
	LotAction status;

	@ManyToOne(fetch = FetchType.EAGER,
	        optional = false)
	@NotNull
	LotHistory history;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER,
			optional = false)
	Purchase purchase;

	public void setPurchase(Purchase p) {
		if (purchase != null) purchase.getLots().remove(this);
		purchase = p;
		if (purchase != null) purchase.getLots().add(this);
	}

	@ManyToOne
	Box location;

	public void setLocation(Box l) {
		if (location != null) location.getLots().remove(this);
		location = l;
		if (location != null) location.getLots().add(this);
	}

	@ManyToOne
	Requirement usedBy;

	public void setUsedBy(Requirement r) {
		if (usedBy != null) usedBy.getLots().remove(this);
		usedBy = r;
		if (usedBy != null) usedBy.getLots().add(this);
	}

	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime expiration;

	@ElementCollection
	Set<String> serials = new THashSet<>();

    public long usedCount() {
        return isCanBeAssigned() ? 0L : count;
    }

    public Long freeCount() {
        return getCount() - usedCount();
    }

	public LotHistory recordChange(Lot update, User performedBy, UuidGenerator uuidGenerator) {
		LotHistory h = new LotHistory();
		h.setId(uuidGenerator.generate());
		h.setPrevious(getHistory());
		h.setPerformedBy(performedBy);
		h.setAction(LotAction.EVENT);

		setHistory(h);

		return h;
	}

	public static Lot delivery(Purchase purchase, UUID uuid, Long count,
							   Box location, DateTime expiration, User performedBy, UuidGenerator uuidGenerator) {
		Lot l = new Lot();
		l.setOwner(purchase.getOwner());
		l.setId(uuid);
		l.setLocation(location);
		l.setCount(count);
		l.setPurchase(purchase);
		l.setExpiration(expiration);
		l.setStatus(LotAction.DELIVERY);

		LotHistory h = l.recordChange(null, performedBy, uuidGenerator);
		h.setLocation(location);

		return l;
	}

	public boolean isCanBeSoldered() {
		return isValid()
                && getUsedBy() != null
				&& !getStatus().equals(LotAction.DESTROYED)
				&& !getStatus().equals(LotAction.SOLDERED);
	}

	public boolean isCanBeUnsoldered() {
		return isValid()
                && getStatus().equals(LotAction.SOLDERED);
	}

	public boolean isCanBeAssigned() {
		return isValid()
                && getUsedBy() == null
				&& !getStatus().equals(LotAction.DESTROYED);
	}

	public boolean isCanBeUnassigned() {
		return isCanBeSoldered();
	}

    public boolean isCanBeSplit() {
        return isValid()
                && EnumSet.of(LotAction.SPLIT, LotAction.DELIVERY, LotAction.SOLDERED,
				LotAction.UNASSIGNED, LotAction.EVENT, LotAction.MOVED).contains(getStatus());
    }

    public boolean isCanBeMoved() {
        return isValid()
                && (isCanBeAssigned() || isCanBeSoldered());
    }

    /**
     * Return true if this Lot record is currently a valid Lot.
     * Return false if the Lot represents a destroyed part and also when this record
     * represents a historical state only.
     */
    public boolean isValid() {
        return !getStatus().equals(LotAction.DESTROYED);
    }

	@Override
	public String getName() {
		return getType().getName();
	}

	@Override
	public String getSummary() {
		return getPurchase().getTransaction().getName();
	}

	@Override
	public String getBaseUrl() {
		return "lots";
	}

	public boolean canBeDeleted() {
		return false;
	}

	public boolean canBeUpdated() {
		return false;
	}

	@Transient
	public Type getType() {
		return getPurchase() == null ? null : getPurchase().getType();
	}

	@Override
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		if (!(update0 instanceof Lot)) {
			throw new IllegalArgumentException();
		}

		Lot update = (Lot)update0;

		if (update.getStatus() != getStatus()) {
			if (update.getStatus() == LotAction.SOLDERED
					&& !isCanBeSoldered()) {
				throw new OperationNotPermitted();
			}

			if (update.getStatus() == LotAction.UNSOLDERED
					&& !isCanBeUnsoldered()) {
				throw new OperationNotPermitted();
			}
		}

		update(update.getStatus(), this::setStatus);

		if (!Objects.equals(update.getCount(), getCount())
				&& !isCanBeSplit()) {
			throw new OperationNotPermitted();
		}

		if (update.getCount().compareTo(getCount()) > 0) {
			throw new OperationNotPermitted();
		}

		update(update.getCount(), this::setCount);

		if (!Objects.equals(update.getLocation(), getLocation())
				&& !isCanBeMoved()) {
			throw new OperationNotPermitted();
		}

		update(update.getLocation(), this::setLocation);

		if (!Objects.equals(update.getUsedBy(), getUsedBy())
				&& update.getUsedBy() == null
				&& !isCanBeUnassigned()) {
			throw new OperationNotPermitted();
		}

		if (!Objects.equals(update.getUsedBy(), getUsedBy())
				&& getUsedBy() == null
				&& !isCanBeAssigned()) {
			throw new OperationNotPermitted();
		}

		update(update.getUsedBy(), this::setUsedBy);

		update(update.getSerials(), this::setSerials);

		super.updateFrom(update);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkItem(relinker, getPurchase(), this::setPurchase);
		relinkItem(relinker, getLocation(), this::setLocation);
		relinkItem(relinker, getUsedBy(), this::setUsedBy);
		relinkItem(relinker, getHistory(), this::setHistory);
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
