package org.marsik.elshelves.api.entities.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

public enum LotAction {
    DELIVERY,
    MIX,
	SPLIT,
    MOVED,
    ASSIGNED,
    UNASSIGNED,
	SOLDERED,
	UNSOLDERED,
	DESTROYED,
    FIXED,
    EVENT,
    FINISHED,
    REOPENED,
    MIXED; // MIXED state is used when the lot was mixed with other lots and it is not possible to distinguish the different lots anymore (MixedLot was created)

    @JsonCreator
    public static LotAction forValue(JsonNode s) {
        if (s.isObject()) {
            return s.get("id") == null ? EVENT : LotAction.valueOf(s.get("id").asText());
        } else if (s.isTextual()) {
            return LotAction.valueOf(s.asText());
        } else if (s.isNull()) {
            return null;
        } else if (s.isNumber()) {
            return LotAction.values()[s.asInt()];
        } else {
            return null;
        }
    }
}
