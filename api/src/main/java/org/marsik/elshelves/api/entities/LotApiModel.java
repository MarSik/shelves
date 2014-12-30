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
import org.marsik.elshelves.api.entities.deserializers.BoxIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.LotIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.PartTypeIdDeserializer;
import org.marsik.elshelves.api.entities.deserializers.UserIdDeserializer;
import org.marsik.elshelves.api.entities.fields.LotAction;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@EmberModelName("lot")
public class LotApiModel extends AbstractEntityApiModel {
    UUID id;
    Date created;
	@Min(1)
    Long count;

    PartTypeApiModel type;
    BoxApiModel location;

	@NotNull
    LotApiModel previous;
    List<LotApiModel> next;

    UserApiModel belongsTo;

	LotAction action;
	UserApiModel performedBy;

    @Override
    @EmberIgnore
    public Map<String, String> getLinks() {
        Map<String, String> links = new THashMap<String, String>();
        links.put("next", "next");
        return links;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
    public PartTypeApiModel getType() {
        return type;
    }

    @JsonSetter
	@JsonDeserialize(using = PartTypeIdDeserializer.class)
    public void setType(PartTypeApiModel type) {
        this.type = type;
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

    @JsonIdentityReference(alwaysAsId = true)
    public List<LotApiModel> getNext() {
        return next;
    }

    @JsonSetter
	@JsonDeserialize(contentUsing = LotIdDeserializer.class)
    public void setNext(List<LotApiModel> next) {
        this.next = next;
    }

    @JsonIdentityReference(alwaysAsId = true)
    public UserApiModel getBelongsTo() {
        return belongsTo;
    }

    @JsonSetter
	@JsonDeserialize(using = UserIdDeserializer.class)
    public void setBelongsTo(UserApiModel belongsTo) {
        this.belongsTo = belongsTo;
    }

	public LotAction getAction() {
		return action;
	}

	public void setAction(LotAction action) {
		this.action = action;
	}

	@JsonIdentityReference(alwaysAsId = true)
	public UserApiModel getPerformedBy() {
		return performedBy;
	}

	@JsonSetter
	@JsonDeserialize(using = UserIdDeserializer.class)
	public void setPerformedBy(UserApiModel performedBy) {
		this.performedBy = performedBy;
	}
}
