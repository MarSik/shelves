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
import org.springframework.data.annotation.CreatedDate;

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

	@NotNull
	@Enumerated(EnumType.STRING)
	LotAction status;

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

	public LotHistory recordChange(Lot update, User performedBy, UuidGenerator uuidGenerator) {
		LotHistory.LotHistoryBuilder h = LotHistory.builder();

		LotAction action = LotAction.EVENT;

		if (update == null) {
			action = LotAction.DELIVERY;
		} else if (update.getStatus() == getStatus()) {
			action = update.getStatus();
		} else if (!update.getLocation().equals(getLocation())) {
			action = LotAction.MOVED;
		} else if (!update.getUsedBy().equals(getUsedBy())) {
			action = update.getUsedBy() == null ? LotAction.UNASSIGNED : LotAction.ASSIGNED;
		}

		h.id(uuidGenerator.generate())
				.previous(getHistory())
				.performedBy(performedBy)
				.action(action)
				.created(new DateTime());

		setHistory(h.build());

		return getHistory();
	}

	public boolean isCanBeSoldered() {
		return isValid()
                && getUsedBy() != null
				&& !LotAction.DESTROYED.equals(getStatus())
				&& !LotAction.SOLDERED.equals(getStatus());
	}

	public boolean isCanBeUnsoldered() {
		return isValid()
                && LotAction.SOLDERED.equals(getStatus());
	}

	public boolean isCanBeAssigned() {
		return isValid()
                && getUsedBy() == null
				&& LotAction.DESTROYED.equals(getStatus());
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
        return getStatus() != null && !getStatus().equals(LotAction.DESTROYED);
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

		return willUpdate(getStatus(), update.getStatus())
				|| willUpdate(getCount(), update.getCount())
				|| willUpdate(getLocation(), update.getLocation())
				|| willUpdate(getUsedBy(), update.getUsedBy());
	}

	@Override
	public LotHistory createRevision(UuidGenerator uuidGenerator, User performedBy) {
		LotHistory h = new LotHistory();
		h.setId(uuidGenerator.generate());
		h.setPrevious(getHistory());
		h.setPerformedBy(performedBy);
		h.setAction(LotAction.EVENT);
		h.setValidSince(new DateTime());
		return h;
	}

	@Override
	public LotHistory getPreviousRevision() {
		return getHistory();
	}

	@Override
	public void setPreviousRevision(LotHistory revision) {
		setHistory(revision);
	}

	public void unlinkWithStatus(LotAction action) {
		setStatus(action);
		setUsedBy(null);
		setLocation(null);
	}
}
