package org.marsik.elshelves.api.entities.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.ember.EmberModelName;

@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@EmberModelName("footprinttype")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public enum FootprintType {
    WIRE,
    TH,
    SMD,
    CONNECTOR;

    public String getId() {
        return name();
    }

    public String getName() {
        return name();
    }

    @JsonCreator
    public static FootprintType forValue(String s) {
        return FootprintType.valueOf(s);
    }

    @JsonProperty("_type")
    public final String emberType = "footprinttype";
}
