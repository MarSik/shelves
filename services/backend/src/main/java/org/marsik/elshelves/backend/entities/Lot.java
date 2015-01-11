package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.fields.LotAction;
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
public class Lot extends LotBase {
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

		if (requirement == null) {
			setAction(LotAction.UNSOLDERED);
		} else {
			setAction(LotAction.SOLDERED);
		}
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

	public SplitResult split(Long count, User performedBy, UuidGenerator uuidGenerator) {
		// Already used
		if (getNext().iterator().hasNext()) {
			return null;
		}

		// Not available for split
		if (!EnumSet.of(LotAction.SPLIT, LotAction.DELIVERY).contains(getAction())) {
			return null;
		}

		// Not enough elements
		if (getCount() < count) {
			return null;
		}

		// No split needed, the exact amount is available
		if (getCount().equals(count)) {
			return new SplitResult(this, null);
		}

		Lot requested = new Lot(uuidGenerator.generate(), performedBy, count, this);
		Lot remainder = new Lot(uuidGenerator.generate(), performedBy, getCount() - count, this);

		return new SplitResult(requested, remainder);
	}

	public Lot solder(User performedBy, UuidGenerator uuidGenerator) {
		return new Lot(uuidGenerator.generate(), performedBy, LotAction.SOLDERED, this);
	}

	public Lot destroy(User performedBy, UuidGenerator uuidGenerator) {
		return new Lot(uuidGenerator.generate(), performedBy, LotAction.DESTROYED, this);
	}

	public Lot solder(User performedBy, UuidGenerator uuidGenerator, Requirement where) {
		return new Lot(uuidGenerator.generate(), performedBy, where, this);
	}

	public Lot unsolder(User performedBy, UuidGenerator uuidGenerator, Requirement where) {
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
}
