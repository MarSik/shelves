package org.marsik.elshelves.backend.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.interfaces.Relinker;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.UuidGenerator;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@Entity
@Data
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Lot extends OwnedEntity implements StickerCapable {
	public Lot() {
	}

	@NotNull
	@Min(1)
	Long count;

	@NotNull
	LotAction status;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			fetch = FetchType.EAGER,
	        optional = false)
	@NotNull
	LotHistory history;

	@NotNull
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
			fetch = FetchType.EAGER,
			optional = false)
	Purchase purchase;

	public void setPurchase(Purchase p) {
		if (purchase != null) purchase.getLots().add(this);
		purchase = p;
		if (purchase != null) purchase.getLots().add(this);
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Box location;

	public void setLocation(Box l) {
		if (location != null) location.getLots().remove(this);
		location = l;
		if (location != null) location.getLots().add(this);
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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

	public static class SplitResult {
		final Lot requested;
		final Lot remainder;

		public SplitResult(Lot requested, Lot remainder) {
			this.requested = requested;
			this.remainder = remainder;
		}

		public Lot getRequested() {
			return requested;
		}

		public Lot getRemainder() {
			return remainder;
		}
	}

	public Lot take(Long count, User performedBy, UuidGenerator uuidGenerator, Requirement requirement) {
		// Not available for split
		if (!isCanBeSplit()) {
			return null;
		}

        // Use the exact amount or all if not enough available
        if (getCount() <= count) {
            count = getCount();
        }

		Lot result = null;

        // No remainder..
        if (getCount().equals(count)) {
            result = this;
        } else {
			// Create the taken Lot and substract the count
			setCount(getCount() - count);

			result = new Lot();
			result.setId(uuidGenerator.generate());
			result.setHistory(getHistory());
			result.setCount(count);
			result.setStatus(getStatus());
			result.setLocation(getLocation());
			result.setUsedBy(getUsedBy());
			result.setExpiration(getExpiration());

			Set<String> serials = new THashSet<>();
			getSerials().stream().skip(getCount() - count).forEach(new Consumer<String>() {
				@Override
				public void accept(String s) {
					serials.add(s);
				}
			});

			result.setSerials(serials); // TODO take a specific serials
			getSerials().removeAll(result.getSerials());
		}

		if (requirement != null && result.isCanBeAssigned()) {
			result.recordChange(LotAction.ASSIGNED, performedBy, uuidGenerator);
		}

		return result;
	}

	protected Lot copy(Long count) {
		Lot l = new Lot();
		l.setCount(count);
		return l;
	}

    public LotHistory move(User performedBy, Box location, UuidGenerator uuidGenerator) {
		setLocation(location);

		LotHistory h = recordChange(LotAction.MOVED, performedBy, uuidGenerator);
		h.setLocation(location);

		return h;
    }

	public LotHistory solder(User performedBy, Requirement requirement, UuidGenerator uuidGenerator) {
		if (requirement != null) {
			assign(performedBy, requirement, uuidGenerator);
		}

		return recordChange(LotAction.SOLDERED, performedBy, uuidGenerator);
	}

    public LotHistory unsolder(User performedBy, UuidGenerator uuidGenerator) {
		return recordChange(LotAction.UNSOLDERED, performedBy, uuidGenerator);
    }

	public LotHistory destroy(User performedBy, UuidGenerator uuidGenerator) {
		return recordChange(LotAction.DESTROYED, performedBy, uuidGenerator);
	}

	public LotHistory assign(User performedBy, Requirement where, UuidGenerator uuidGenerator) {
		setUsedBy(where);

		LotHistory h = recordChange(LotAction.ASSIGNED, performedBy, uuidGenerator);
		h.setAssignedTo(where);

		return h;
	}

	public LotHistory unassign(User performedBy, UuidGenerator uuidGenerator) {
		setUsedBy(null);

		LotHistory h = recordChange(LotAction.UNASSIGNED, performedBy, uuidGenerator);
		h.setAssignedTo(null);

		return h;
	}

	private LotHistory recordChange(LotAction action, User performedBy, UuidGenerator uuidGenerator) {
		setStatus(action);

		LotHistory h = new LotHistory();
		h.setId(uuidGenerator.generate());
		h.setPrevious(getHistory());
		setHistory(h);
		h.setPerformedBy(performedBy);
		h.setAction(action);

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

		LotHistory h = l.recordChange(LotAction.DELIVERY, performedBy, uuidGenerator);
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
                && EnumSet.of(LotAction.SPLIT, LotAction.DELIVERY, LotAction.UNASSIGNED, LotAction.EVENT, LotAction.MOVED).contains(getStatus());
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
	public void relink(Relinker relinker) {
		relinkItem(relinker, getPurchase(), this::setPurchase);
		relinkItem(relinker, getLocation(), this::setLocation);
		relinkItem(relinker, getUsedBy(), this::setUsedBy);
		super.relink(relinker);
	}
}
