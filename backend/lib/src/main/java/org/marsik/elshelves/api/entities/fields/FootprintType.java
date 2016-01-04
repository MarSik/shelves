package org.marsik.elshelves.api.entities.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.JsonNode;
import org.marsik.elshelves.ember.EmberModelName;

@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public enum FootprintType {
    WIRE,
    TH,
    SMD,
    CONNECTOR,
    UNKNOWN;

    public String getId() {
        return name();
    }

    public String getName() {
        return name();
    }

    @JsonCreator
    public static FootprintType forValue(JsonNode s) {
        if (s.isObject()) {
            return s.get("id") == null ? UNKNOWN : FootprintType.valueOf(s.get("id").asText());
        } else if (s.isTextual()) {
            return FootprintType.valueOf(s.asText());
        } else if (s.isNull()) {
            return null;
        } else if (s.isNumber()) {
            return FootprintType.values()[s.asInt()];
        } else {
            return null;
        }
    }

    @JsonProperty("_type")
    public final String emberType = "footprinttype";
}
