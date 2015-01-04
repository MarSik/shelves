package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gnu.trove.map.hash.THashMap;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.fields.LotAction;
import org.marsik.elshelves.api.entities.idresolvers.LotIdResolver;

import java.util.Map;
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

	@Sideload
    BoxApiModel location;

	@Sideload
    LotApiModel previous;

	LotAction action;

	@Sideload
	PurchaseApiModel purchase;

	@Sideload
	RequirementApiModel usedBy;

    @Override
    @EmberIgnore
    public Map<String, String> getLinks() {
        Map<String, String> links = new THashMap<String, String>();
        links.put("next", "next");
		links.put("purchase", "purchase");
        return links;
    }

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
}
