package org.marsik.elshelves.api.entities.fields;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LotAction {
    DELIVERY,
	SPLIT,
    MOVED,
    ASSIGNED,
    UNASSIGNED,
	SOLDERED,
	UNSOLDERED,
	DESTROYED,
    EVENT,
    FINISHED,
    REOPENED;

    @JsonCreator
    public static LotAction forValue(String s) {
        return LotAction.valueOf(s);
    }
}
