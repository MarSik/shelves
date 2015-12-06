package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.idresolvers.LotHistoryIdResolver;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.LotAction;

import java.util.UUID;

@Getter
@Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id", resolver = LotHistoryIdResolver.class)
@JsonTypeName("history")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "_type",
        visible = true,
        defaultImpl = LotHistoryApiModel.class)
public class LotHistoryApiModel extends AbstractEntityApiModel {
    public LotHistoryApiModel(UUID uuid) {
        super(uuid);
    }

    public LotHistoryApiModel() {
    }

    public LotHistoryApiModel(String uuid) {
        super(uuid);
    }

    @JsonProperty("previous")
    LotHistoryApiModel previous;

    DateTime validSince;
    LotAction action;

    @JsonProperty("performedBy")
    UserApiModel performedBy;

    @JsonProperty("location")
    BoxApiModel location;

    @JsonProperty("assignedTo")
    RequirementApiModel assignedTo;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
