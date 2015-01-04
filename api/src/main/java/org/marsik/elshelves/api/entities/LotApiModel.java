package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gnu.trove.map.hash.THashMap;
import nl.marcus.ember.EmberIgnore;
import org.marsik.elshelves.api.ember.EmberModelName;
import org.marsik.elshelves.api.ember.Sideload;
import org.marsik.elshelves.api.entities.deserializers.BoxIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.LotIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.PartTypeIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.PurchaseIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.RequirementIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;
import org.marsik.elshelves.api.entities.fields.LotAction;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("lot")
public class LotApiModel extends LotBaseApiModel {

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
	@JsonDeserialize(using = BoxIdDeserializer.class)
    public void setLocation(BoxApiModel location) {
        this.location = location;
    }

	@JsonIdentityReference(alwaysAsId = true)
    public LotApiModel getPrevious() {
        return previous;
    }

    @JsonSetter
	@JsonDeserialize(using = LotIdDeserializer.class)
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
	@JsonDeserialize(using = PurchaseIdDeserializer.class)
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
	@JsonDeserialize(using = RequirementIdDeserializer.class)
	public void setUsedBy(RequirementApiModel usedBy) {
		this.usedBy = usedBy;
	}
}
