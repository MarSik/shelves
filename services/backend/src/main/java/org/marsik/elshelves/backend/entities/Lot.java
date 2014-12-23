package org.marsik.elshelves.backend.entities;

import org.marsik.elshelves.api.entities.LotApiModel;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.backend.dtos.LotSplitResult;
import org.marsik.elshelves.backend.services.UuidGenerator;
import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;

@NodeEntity
public class Lot implements OwnedEntity {
	public Lot() {
	}

	protected Lot(UUID uuid, User performedBy, Long count, Lot previous) {
		this.uuid = uuid;
		this.performedBy = performedBy;
		this.count = count;
		this.previous = previous;
		this.owner = previous.getOwner();
		this.created = new Date();
		this.type = previous.getType();
		this.next = new ArrayList<>();
		this.action = LotAction.SPLIT;
	}

	protected Lot(UUID uuid, User performedBy, LotAction action, Lot previous) {
		this.action = action;
		this.previous = previous;
		this.performedBy = performedBy;
		this.uuid = uuid;
		this.count = previous.count;
		this.owner = previous.getOwner();
		this.created = new Date();
		this.type = previous.getType();
		this.next = new ArrayList<>();
	}

	@Indexed
	UUID uuid;

	@RelatedTo(type = "OWNS", direction = Direction.INCOMING, enforceTargetType = true)
	User owner;

	Date created;
	Long count;

	@RelatedTo(type = "OF_TYPE")
	Type type;

	@RelatedTo(type = "TAKEN_FROM")
	Lot previous;

	@RelatedTo(type = "TAKEN_FROM", direction = Direction.INCOMING)
	Iterable<Lot> next;

	LotAction action;

	@RelatedTo(type = "PERFORMED_BY")
	User performedBy;

	@RelatedTo(type = "LOCATED_AT")
	Box location;

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Lot getPrevious() {
		return previous;
	}

	public void setPrevious(Lot previous) {
		this.previous = previous;
	}

	public Iterable<Lot> getNext() {
		return next;
	}

	public void setNext(Iterable<Lot> next) {
		this.next = next;
	}

	public LotAction getAction() {
		return action;
	}

	public void setAction(LotAction action) {
		this.action = action;
	}

	public User getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(User performedBy) {
		this.performedBy = performedBy;
	}

	public Box getLocation() {
		return location;
	}

	public void setLocation(Box location) {
		this.location = location;
	}

	public boolean canBeDeleted() {
		return false;
	}

	public boolean canBeUpdated() {
		return false;
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
		if (!EnumSet.of(LotAction.SPLIT, LotAction.PURCHASE).contains(getAction())) {
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
}
