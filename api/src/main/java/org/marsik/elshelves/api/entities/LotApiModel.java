package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.api.entities.idresolvers.LotIdResolver;

import java.util.UUID;

/**
 * This entity represents one step in history of
 * a group of identical parts. It is read only!
 *
 * Any time a part is taken from the Lot, new Lot
 * objects need to be created to represent the
 * resulting two new Lots.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = LotIdResolver.class)
@EmberModelName("lot")
public class LotApiModel extends LotBaseApiModel {
	public LotApiModel(UUID id) {
		super(id);
	}

	public LotApiModel() {
	}

    BoxApiModel location;

    LotApiModel previous;

	LotAction action;

	PurchaseApiModel purchase;

	RequirementApiModel usedBy;

	boolean canBeSoldered;
	boolean canBeUnsoldered;
	boolean canBeAssigned;
	boolean canBeUnassigned;
    boolean canBeSplit;
    boolean canBeMoved;
    boolean valid;

	@JsonIdentityReference(alwaysAsId = true)
    public BoxApiModel getLocation() {
        return location;
    }

    @JsonSetter
    public void setLocation(BoxApiModel location) {
        this.location = location;
    }

	@JsonIdentityReference(alwaysAsId = true)
    public LotApiModel getPrevious() {
        return previous;
    }

    @JsonSetter
    public void setPrevious(LotApiModel previous) {
        this.previous = previous;
    }

	public LotAction getAction() {
		return action;
	}

	public void setAction(LotAction action) {
		this.action = action;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public PurchaseApiModel getPurchase() {
		return purchase;
	}

	@JsonSetter
	public void setPurchase(PurchaseApiModel purchase) {
		this.purchase = purchase;
	}

	@JsonIgnore
	public PartTypeApiModel getType() {
		return getPurchase().getType();
	}

	@JsonIdentityReference(alwaysAsId = true)
	public RequirementApiModel getUsedBy() {
		return usedBy;
	}

	@JsonSetter
	public void setUsedBy(RequirementApiModel usedBy) {
		this.usedBy = usedBy;
	}

	public boolean isCanBeSoldered() {
		return canBeSoldered;
	}

	public void setCanBeSoldered(boolean canBeSoldered) {
		this.canBeSoldered = canBeSoldered;
	}

	public boolean isCanBeUnsoldered() {
		return canBeUnsoldered;
	}

	public void setCanBeUnsoldered(boolean canBeUnsoldered) {
		this.canBeUnsoldered = canBeUnsoldered;
	}

	public boolean isCanBeAssigned() {
		return canBeAssigned;
	}

	public void setCanBeAssigned(boolean canBeAssigned) {
		this.canBeAssigned = canBeAssigned;
	}

	public boolean isCanBeUnassigned() {
		return canBeUnassigned;
	}

	public void setCanBeUnassigned(boolean canBeUnassigned) {
		this.canBeUnassigned = canBeUnassigned;
	}

    public boolean isCanBeSplit() {
        return canBeSplit;
    }

    public void setCanBeSplit(boolean canBeSplit) {
        this.canBeSplit = canBeSplit;
    }

    public boolean isCanBeMoved() {
        return canBeMoved;
    }

    public void setCanBeMoved(boolean canBeMoved) {
        this.canBeMoved = canBeMoved;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
