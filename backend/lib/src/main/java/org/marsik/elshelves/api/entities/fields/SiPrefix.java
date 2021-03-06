package org.marsik.elshelves.api.entities.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.marsik.elshelves.ember.EmberModelName;

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
public enum SiPrefix {
    // SI base 10 prefixes
	ATTO("a", 10, -18),
	FEMTO("f", 10, -15),
	PICO("p", 10, -12),
	NANO("n", 10, -9),
	MICRO("µ", 10, -6),
	MILLI("m", 10, -3),
	NONE("", 10, 0),
	DECA("da", 10, 1),
	HECTO("h", 10, 2),
	KILO("k", 10, 3),
	MEGA("M", 10, 6),
	GIGA("G", 10, 9),
	TERA("T", 10, 12),
	PETA("P", 10, 15),
	EXA("E", 10, 18),
	ZETTA("Z", 10, 21),
	YOTTA("Y", 10, 24),

    // SI base 2 prefixes
    KIBI("Ki", 2, 10),
    MIBI("Mi", 2, 20),
    GIBI("Gi", 2, 30),
    TIBI("Ti", 2, 40),
    PIBI("Pi", 2, 50)
    ;

	/**
	 * Prefix letter(s)
	 */
	final String prefix;

	/**
	 * Power of <base> representation of the actual base
	 *
	 * ex:
	 * 1 -> 0
	 * 10 -> 1
	 * 1 000 000 -> 6
	 * 0.000 000 001 -> -9
	 */
	final int power;
    final int base;

	SiPrefix(String prefix, int base, int power) {
		this.prefix = prefix;
		this.power = power;
        this.base = base;
	}

    public String getPrefix() {
        return prefix;
    }

    public int getPower() {
        return power;
    }

    public int getBase() {
        return base;
    }

    public String getId() {
        return name();
    }

	@JsonCreator
	public static SiPrefix forValue(JsonNode s) {
		if (s.isObject()) {
			return s.get("id") == null ? NONE : SiPrefix.valueOf(s.get("id").asText());
		} else if (s.isTextual()) {
			return SiPrefix.valueOf(s.asText());
		} else if (s.isNull()) {
			return null;
		} else if (s.isNumber()) {
			return SiPrefix.values()[s.asInt()];
		} else {
			return null;
		}
	}

	@JsonProperty("_type")
	public final String emberType = "siprefix";
}
