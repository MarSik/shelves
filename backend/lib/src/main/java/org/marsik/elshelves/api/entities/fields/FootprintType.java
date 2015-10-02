package org.marsik.elshelves.api.entities.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.marsik.elshelves.ember.EmberModelName;

@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@EmberModelName("footprinttype")
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
}
