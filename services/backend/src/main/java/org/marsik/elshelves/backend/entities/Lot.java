package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.services.StickerCapable;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;

@NodeEntity
public class Lot extends LotBase implements StickerCapable {
	public Lot() {
	}

	public static Lot delivery(Purchase purchase, UUID uuid, Long count, Box location, User performedBy) {
		Lot l = new Lot();
		l.setOwner(purchase.getOwner());
		l.setAction(LotAction.DELIVERY);
		l.setUuid(uuid);
		l.setLocation(location);
		l.setCount(count);
		l.setPurchase(purchase);
		l.setCreated(new Date());
		l.setPerformedBy(performedBy);
		return l;
	}

	protected Lot(UUID uuid, User performedBy, Long count, Lot previous) {
		setUuid(uuid);
		setPerformedBy(performedBy);
		setCount(count);
		setPrevious(previous);
		setOwner(previous.getOwner());
		setCreated(new Date());
		setNext(new ArrayList<Lot>());
		setAction(LotAction.SPLIT);
		setUsedBy(previous.getUsedBy());
        setLocation(previous.getLocation());
	}

    protected Lot(UUID uuid, User performedBy, Long count, Requirement requirement, Lot previous) {
        setUuid(uuid);
        setPerformedBy(performedBy);
        setCount(count);
        setPrevious(previous);
        setOwner(previous.getOwner());
        setCreated(new Date());
        setNext(new ArrayList<Lot>());
        setAction(LotAction.SPLIT);
        if (requirement == null) {
            setUsedBy(previous.getUsedBy());
        } else {
            setUsedBy(requirement);
        }
        setLocation(previous.getLocation());
    }

	protected Lot(UUID uuid, User performedBy, LotAction action, Lot previous) {
		setAction(action);
		setPrevious(previous);
		setPerformedBy(performedBy);
		setUuid(uuid);
		setCount(previous.count);
		setOwner(previous.getOwner());
		setCreated(new Date());
		setNext(new ArrayList<Lot>());
		setUsedBy(previous.getUsedBy());
        setLocation(previous.getLocation());
	}

    protected Lot(UUID uuid, User performedBy, LotAction action, Requirement requirement, Lot previous) {
        setAction(action);
        setPrevious(previous);
        setPerformedBy(performedBy);
        setUuid(uuid);
        setCount(previous.count);
        setOwner(previous.getOwner());
        setCreated(new Date());
        setNext(new ArrayList<Lot>());
        if (requirement == null) {
            setUsedBy(previous.getUsedBy());
        } else {
            setUsedBy(requirement);
        }
        setLocation(previous.getLocation());
    }

	protected Lot(UUID uuid, User performedBy, Requirement requirement, Lot previous) {
		setPrevious(previous);
		setPerformedBy(performedBy);
		setUuid(uuid);
		setCount(previous.count);
		setOwner(previous.getOwner());
		setCreated(new Date());
		setNext(new ArrayList<Lot>());
		setUsedBy(requirement);
        setLocation(previous.getLocation());

		if (requirement == null) {
			setAction(LotAction.UNASSIGNED);
		} else {
			setAction(LotAction.ASSIGNED);
		}
	}

    protected Lot(UUID uuid, User performedBy, Box location, Lot previous) {
        setPrevious(previous);
        setPerformedBy(performedBy);
        setUuid(uuid);
        setCount(previous.count);
        setOwner(previous.getOwner());
        setCreated(new Date());
        setNext(new ArrayList<Lot>());
        setUsedBy(previous.getUsedBy());
        setAction(LotAction.MOVED);
        setLocation(location);
    }

	@RelatedTo(type = "TAKEN_FROM", enforceTargetType = true)
	Lot previous;

	@RelatedTo(type = "TAKEN_FROM", direction = Direction.INCOMING, enforceTargetType = true)
	Iterable<Lot> next;

	@RelatedTo(type = "PERFORMED_BY", enforceTargetType = true)
	User performedBy;

	@NotNull
	@RelatedTo(type = "PURCHASED_AS")
	Purchase purchase;

	@NotNull
	LotAction action;

	@RelatedTo(type = "LOCATED_AT")
	Box location;

	@RelatedTo(type = "USES", direction = Direction.INCOMING)
	Requirement usedBy;

	public Lot getPrevious() {
		return previous;
	}

	public void setPrevious(Lot previous) {
		this.previous = previous;
	}

	public LotAction getAction() {
		return action;
	}

	public void setAction(LotAction action) {
		this.action = action;
	}

	public Box getLocation() {
		return location;
	}

	public void setLocation(Box location) {
		this.location = location;
	}

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

	public class SplitResult {
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

		Lot requested = new Lot(uuidGenerator.generate(), performedBy, count, this);

        // No remainder..
        if (getCount() == count) {
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

	public Purchase getPurchase() {
		return purchase;
	}

	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}

	@Override
	public Type getType() {
		return getPurchase().getType();
	}


	public Iterable<Lot> getNext() {
		return next;
	}

	public void setNext(Iterable<Lot> next) {
		this.next = next;
	}

	public User getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(User performedBy) {
		this.performedBy = performedBy;
	}

	public Requirement getUsedBy() {
		return usedBy;
	}

	public void setUsedBy(Requirement usedBy) {
		this.usedBy = usedBy;
	}

	public boolean isCanBeSoldered() {
		return getUsedBy() != null
				&& !getAction().equals(LotAction.DESTROYED)
				&& !getAction().equals(LotAction.SOLDERED);
	}

	public boolean isCanBeUnsoldered() {
		return getAction().equals(LotAction.SOLDERED);
	}

	public boolean isCanBeAssigned() {
		return getUsedBy() == null
				&& !getAction().equals(LotAction.DESTROYED);
	}

	public boolean isCanBeUnassigned() {
		return isCanBeSoldered();
	}

    public boolean isCanBeSplit() {
        return EnumSet.of(LotAction.SPLIT, LotAction.DELIVERY).contains(getAction());
    }

    public boolean isCanBeMoved() {
        return isCanBeSoldered();
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
