package org.marsik.elshelves.backend.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.UuidGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;

@Entity
@Data
@ToString(of = {}, callSuper = true)
@EqualsAndHashCode(of = {}, callSuper = true)
public class Lot extends LotBase implements StickerCapable {
	protected Lot() {
	}

    protected Lot(UUID uuid, User performedBy, Lot previous) {
        setUuid(uuid);
        setPerformedBy(performedBy);
        setPrevious(previous);
        setCreated(new DateTime());
        setNext(new ArrayList<Lot>());
        setAction(LotAction.EVENT);

        setOwner(previous.getOwner());
        setCount(previous.getCount());
        setUsedBy(previous.getUsedBy());
        setLocation(previous.getLocation());
        setPurchase(previous.getPurchase());
        setExpiration(previous.getExpiration());
    }

	protected Lot(UUID uuid, User performedBy, Long count, Lot previous) {
        this(uuid, performedBy, previous);
		setCount(count);
		setAction(LotAction.SPLIT);
	}

    protected Lot(UUID uuid, User performedBy, Long count, Requirement requirement, Lot previous) {
        this(uuid, performedBy, previous);
        setCount(count);

        if (requirement == null) {
            setUsedBy(previous.getUsedBy());
            setAction(LotAction.SPLIT);
        } else {
            setUsedBy(requirement);
            setAction(LotAction.ASSIGNED);
        }
    }

	protected Lot(UUID uuid, User performedBy, LotAction action, Lot previous) {
        this(uuid, performedBy, previous);
		setAction(action);
	}

    protected Lot(UUID uuid, User performedBy, LotAction action, Requirement requirement, Lot previous) {
        this(uuid, performedBy, previous);
        setAction(action);

        if (requirement == null) {
            setUsedBy(previous.getUsedBy());
        } else {
            setUsedBy(requirement);
        }
    }

	protected Lot(UUID uuid, User performedBy, Requirement requirement, Lot previous) {
        this(uuid, performedBy, previous);
		setUsedBy(requirement);

		if (requirement == null) {
			setAction(LotAction.UNASSIGNED);
		} else {
			setAction(LotAction.ASSIGNED);
		}
	}

    protected Lot(UUID uuid, User performedBy, Box location, Lot previous) {
        this(uuid, performedBy, previous);
        setAction(LotAction.MOVED);
        setLocation(location);
    }

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Lot previous;

	@OneToMany(mappedBy = "previous",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Collection<Lot> next;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	User performedBy;

	@NotNull
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Purchase purchase;

	@NotNull
	LotAction action;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Box location;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	Requirement usedBy;

	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime expiration;

	String serial;

    public Long usedCount() {
        long count = 0;
        for (Lot l: getNext()) {
            count += l.getCount();
        }
        return count;
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

	public SplitResult split(Long count, User performedBy, UuidGenerator uuidGenerator, Requirement requirement) {
		// Already used
		if (getNext().iterator().hasNext()) {
			return null;
		}

		// Not available for split
		if (!isCanBeSplit()) {
			return null;
		}

        // No assignment required
		// Not enough elements, use all
		// No split needed, the exact amount is available
		if (requirement == null && getCount() <= count) {
			return new SplitResult(this, null);
		}

        // Assignment required, use the exact amount or all if
        // not enough available
        if (getCount() <= count) {
            count = getCount();
        }

		Lot requested = new Lot(uuidGenerator.generate(), performedBy, count, requirement, this);

        // No remainder..
        if (getCount().equals(count)) {
            return new SplitResult(requested, null);
        }

		Lot remainder = new Lot(uuidGenerator.generate(), performedBy, getCount() - count, this);
		return new SplitResult(requested, remainder);
	}

    public Lot move(User performedBy, UuidGenerator uuidGenerator, Box location) {
        return new Lot(uuidGenerator.generate(), performedBy, location, this);
    }

	public Lot solder(User performedBy, UuidGenerator uuidGenerator, Requirement requirement) {
		return new Lot(uuidGenerator.generate(), performedBy, LotAction.SOLDERED, requirement, this);
	}

    public Lot unsolder(User performedBy, UuidGenerator uuidGenerator) {
        return new Lot(uuidGenerator.generate(), performedBy, LotAction.UNSOLDERED, this);
    }

	public Lot destroy(User performedBy, UuidGenerator uuidGenerator) {
		return new Lot(uuidGenerator.generate(), performedBy, LotAction.DESTROYED, this);
	}

	public Lot assign(User performedBy, UuidGenerator uuidGenerator, Requirement where) {
		return new Lot(uuidGenerator.generate(), performedBy, where, this);
	}

	public Lot unassign(User performedBy, UuidGenerator uuidGenerator) {
		return new Lot(uuidGenerator.generate(), performedBy, (Requirement)null, this);
	}

	public static Lot delivery(Purchase purchase, UUID uuid, Long count, Box location, DateTime expiration, User performedBy) {
		Lot l = new Lot();
		l.setOwner(purchase.getOwner());
		l.setAction(LotAction.DELIVERY);
		l.setUuid(uuid);
		l.setLocation(location);
		l.setCount(count);
		l.setPurchase(purchase);
		l.setCreated(new DateTime());
		l.setPerformedBy(performedBy);
		l.setExpiration(expiration);
		return l;
	}

	public boolean isCanBeSoldered() {
		return isValid()
                && getUsedBy() != null
				&& !getAction().equals(LotAction.DESTROYED)
				&& !getAction().equals(LotAction.SOLDERED);
	}

	public boolean isCanBeUnsoldered() {
		return isValid()
                && getAction().equals(LotAction.SOLDERED);
	}

	public boolean isCanBeAssigned() {
		return isValid()
                && getUsedBy() == null
				&& !getAction().equals(LotAction.DESTROYED);
	}

	public boolean isCanBeUnassigned() {
		return isCanBeSoldered();
	}

    public boolean isCanBeSplit() {
        return isValid()
                && EnumSet.of(LotAction.SPLIT, LotAction.DELIVERY, LotAction.UNASSIGNED, LotAction.EVENT, LotAction.MOVED).contains(getAction());
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
        return !getNext().iterator().hasNext() && !getAction().equals(LotAction.DESTROYED);
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
}
