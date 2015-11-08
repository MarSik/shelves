package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.api.entities.idresolvers.LotIdResolver;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

/**
 * This entity represents one step in history of
 * a group of identical parts. It is read only!
 *
 * Any time a part is taken from the Lot, new Lot
 * objects need to be created to represent the
 * resulting two new Lots.
 */
@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = LotIdResolver.class)
@EmberModelName("lot")
public class LotApiModel extends AbstractEntityApiModel {
	public LotApiModel(UUID id) {
		super(id);
	}

	public LotApiModel() {
	}

    BoxApiModel location;

	LotAction status;

	PurchaseApiModel purchase;

	RequirementApiModel usedBy;

    DateTime expiration;

	/**
	 * Used only for data restore from old data.
 	 */
	DateTime created;

	LotHistoryApiModel history;

	@Min(1)
	@NotNull
	Long count;

	Set<String> serials;

	boolean canBeSoldered;
	boolean canBeUnsoldered;
	boolean canBeAssigned;
	boolean canBeUnassigned;
    boolean canBeSplit;
    boolean canBeMoved;
    boolean valid;

	/**
	 * Serial number
	 * Deprecated: only used for importing older data
	 */
	String serial;

	/**
	 * Previous version of this Lot (history)
	 * Only used for importing older data and requesting a partial Lot
	 */
	@Setter(value = AccessLevel.PRIVATE)
	LotApiModel previous;

	/**
	 * Next version of this Lot (history)
	 * Deprecated: only used for importing older data
	 */
	Set<LotApiModel> next;

	/**
	 * User responsible for the last change
	 * Deprecated: only used for importing older data
	 */
	UserApiModel performedBy;

	/**
	 * Deprecated: only used for importing older data
	 */
	@JsonIdentityReference(alwaysAsId = true)
	public Set<LotApiModel> getNext() {
		return next;
	}

	/**
	 * Deprecated: only used for importing older data
	 */
	@JsonIdentityReference(alwaysAsId = true)
	public UserApiModel getPerformedBy() {
		return performedBy;
	}

	/**
	 * Deprecated: only used for importing older data
	 */
	public LotAction getAction() {
		return status;
	}

	/**
	 * Deprecated: only used for importing older data
	 */
	public void setAction(LotAction action) {
		status = action;
	}

	@JsonIdentityReference(alwaysAsId = true)
    public BoxApiModel getLocation() {
        return location;
    }

	@JsonIdentityReference(alwaysAsId = true)
    public LotApiModel getPrevious() {
        return previous;
    }

	@JsonIdentityReference(alwaysAsId = true)
	public PurchaseApiModel getPurchase() {
		return purchase;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public RequirementApiModel getUsedBy() {
		return usedBy;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public LotHistoryApiModel getHistory() {
		return history;
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
