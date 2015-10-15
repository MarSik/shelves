package org.marsik.elshelves.api.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import org.marsik.elshelves.api.entities.idresolvers.LotHistoryIdResolver;
import org.marsik.elshelves.ember.EmberModelName;
import org.marsik.elshelves.api.entities.fields.LotAction;

import java.util.UUID;

@Data
@EqualsAndHashCode(of = {}, callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = LotHistoryIdResolver.class)
@EmberModelName("history")
public class LotHistoryApiModel extends AbstractEntityApiModel {
    public LotHistoryApiModel(UUID uuid) {
        super(uuid);
    }

    public LotHistoryApiModel() {
    }

    @JsonProperty("previous")
    UUID previousId;

    DateTime created;
    LotAction action;

    @JsonProperty("performedBy")
    UUID performedById;

    @JsonProperty("location")
    UUID locationId;
}