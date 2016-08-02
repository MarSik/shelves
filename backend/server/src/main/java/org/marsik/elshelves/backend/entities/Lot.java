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
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DefaultEmberModel(LotApiModel.class)
public class Lot extends OwnedEntity implements StickerCapable, RevisionsSupport<LotHistory> {
	public Lot(UUID uuid) {
		setId(uuid);
	}

	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	Type type;

	public void setType(Type t) {
		if (type != null) type.getLots().remove(this);
		type = t;
		if (type != null) type.getLots().add(this);
	}

	@NotNull
	@Min(1)
	Long count;

	/**
	 * true if this Lot record is currently a valid Lot.
	 * false if the Lot represents a destroyed part and also when this record
	 * represents a historical state only.
	 */
	Boolean valid;

	/**
	 * Currently soldered (used in circuit). This is more than just
	 * assignment to project, it really marks the part as tainted.
	 */
	Boolean used;

	/**
	 * Contains parts that were used in the past
	 */
	Boolean usedInPast;

	@ManyToOne(fetch = FetchType.LAZY,
	        optional = false)
	@NotNull
	LotHistory history;

	@ManyToOne(fetch = FetchType.LAZY)
	Box location;

	public void setLocation(Box l) {
		if (location != null) location.getLots().remove(this);
		location = l;
		if (location != null) location.getLots().add(this);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	Requirement usedBy;

	public void setUsedBy(Requirement r) {
		if (usedBy != null) usedBy.getLots().remove(this);
		usedBy = r;
		if (usedBy != null) usedBy.getLots().add(this);
	}

	@org.hibernate.annotations.Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime expiration;

	@ElementCollection(fetch = FetchType.LAZY)
	Set<String> serials = new THashSet<>();

    public long usedCount() {
        return isCanBeAssigned() ? 0L : count;
    }

    public Long freeCount() {
        return getCount() - usedCount();
    }

	@Override
	public LotHistory createRevision(UpdateableEntity update0, UuidGenerator uuidGenerator, User performedBy) {
		if (update0 != null && !(update0 instanceof Lot)) {
			throw new IllegalArgumentException();
		}

		Lot update = (Lot) update0;

		LotHistory.LotHistoryBuilder h = LotHistory.builder();

		LotAction action = LotAction.EVENT;

		if (update == null) {
			action = LotAction.DELIVERY;
			h.location(getLocation());
			h.assignedTo(getUsedBy());
		} else if (update.getValid() != null && !Objects.equals(update.getValid(), getValid())) {
			action = update.getValid() ? LotAction.FIXED : LotAction.DESTROYED;
		} else if (update.getUsed() != null && !Objects.equals(update.getUsed(), getUsed())) {
			action = update.getUsed() ? LotAction.SOLDERED : LotAction.UNSOLDERED;
		} else if (!Objects.equals(update.getLocation(), getLocation())) {
			action = LotAction.MOVED;
			h.location(update.getLocation());
		} else if (!Objects.equals(update.getUsedBy(), getUsedBy())) {
			action = update.getUsedBy() == null ? LotAction.UNASSIGNED : LotAction.ASSIGNED;
			h.assignedTo(update.getUsedBy());
		}

		h.id(uuidGenerator.generate())
				.previous(getHistory())
				.performedBy(performedBy)
				.action(action)
				.created(new DateTime());

		return h.build();
	}

	public boolean isCanBeSoldered() {
		return isValid()
                && getUsedBy() != null
				&& !getUsed();
	}

	public boolean isCanBeSolderedTo(@NotNull Requirement requirement) {
		return isValid()
				&& (Objects.equals(requirement, getUsedBy()) || getUsedBy() == null)
				&& requirement != null
				&& !getUsed();
	}

	public boolean isCanBeUnsoldered() {
		return isValid()
                && getUsed();
	}

	public boolean isCanBeAssigned() {
		return isValid()
                && getUsedBy() == null;
	}

	public boolean isCanBeUnassigned() {
		return isCanBeSoldered();
	}

    public boolean isCanBeSplit() {
        return isValid()
                && getUsedBy() == null
				&& !getUsed();
    }

    public boolean isCanBeMoved() {
        return isValid()
                && (isCanBeAssigned() || isCanBeSoldered());
    }

    public boolean isValid() {
        return getValid();
    }

	@Override
	public String getName() {
		return getType().getName();
	}

	@Override
	public String getSummary() {
		return "";
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

	@Override
	public void updateFrom(UpdateableEntity update0) throws OperationNotPermitted {
		if (!(update0 instanceof Lot)) {
			throw new IllegalArgumentException();
		}

		Lot update = (Lot)update0;

		if (update.getUsed() != null
				&& !getUsed()
				&& update.getUsed()
				&& !isCanBeSolderedTo(update.getUsedBy())) {
			throw new OperationNotPermitted();
		}

		if (update.getUsed() != null
				&& getUsed()
				&& !update.getUsed()
				&& !isCanBeUnsoldered()) {
			throw new OperationNotPermitted();
		}

		if (update.getValid() != null
				&& !update.getValid()) {
			update.setUsedBy(null);
			update.setLocation(null);
		}

		update(update.getValid(), this::setValid);
		update(update.getUsed(), this::setUsed);
		setUsedInPast(getUsedInPast() || getUsed());

		if (update.getLocation() != null
				&& !Objects.equals(update.getLocation(), getLocation())
				&& !isCanBeMoved()) {
			throw new OperationNotPermitted();
		}

		update(update.getLocation(), this::setLocation);

		// reset location when soldering to an item
		if (getUsed()) {
			setLocation(null);
		}

		// restore location when unsoldering
		if (!getUsed()
				&& getUsedBy() != null
				&& getLocation() == null) {
			setLocation(getUsedBy().getItem().getLocation());
		}

		if (!Objects.equals(update.getCount(), getCount())
				&& !isCanBeSplit()) {
			throw new OperationNotPermitted();
		}

		if (update.getCount() != null
				&& update.getCount().compareTo(getCount()) > 0) {
			throw new OperationNotPermitted();
		}

		update(update.getCount(), this::setCount);

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

		setUsedBy(update.getUsedBy());

		update(update.getSerials(), this::setSerials);

		super.updateFrom(update);
	}

	@Override
	public void relink(Relinker relinker) {
		relinkItem(relinker, getLocation(), this::setLocation);
		relinkItem(relinker, getUsedBy(), this::setUsedBy);
		relinkItem(relinker, getHistory(), this::setHistory);
		relinkItem(relinker, getType(), this::setType);
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

	@Override
	public boolean isRevisionNeeded(UpdateableEntity update0) {
		if (!(update0 instanceof Lot)) {
			throw new IllegalArgumentException();
		}

		Lot update = (Lot)update0;

		return willUpdate(getValid(), update.getValid())
				|| willUpdate(getUsed(), update.getUsed())
				|| willUpdate(getCount(), update.getCount())
				|| !Objects.equals(getLocation(), update.getLocation())
				|| !Objects.equals(getUsedBy(), update.getUsedBy());
	}

	@Override
	public LotHistory getPreviousRevision() {
		return getHistory();
	}

	@Override
	public void setPreviousRevision(LotHistory revision) {
		setHistory(revision);
	}

	public void invalidate() {
		setValid(false);
		setUsed(false);
		setUsedBy(null);
		setLocation(null);
	}

	@Override
	public Object shallowClone() {
		Lot l = (Lot)super.shallowClone();
		l.setSerials(new THashSet<>(this.getSerials()));
		return l;
	}
}
