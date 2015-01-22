package org.marsik.elshelves.api.entities.fields;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.marsik.elshelves.api.ember.EmberModelName;

@EmberModelName("isoprefix")
public enum IsoSizePrefix {
	ATTO("a", -18),
	FEMTO("f", -15),
	PICO("p", -12),
	NANO("n", -9),
	MICRO("u", -6),
	MILLI("m", -3),
	NONE("", 0),
	DECA("da", 1),
	HECTO("h", 2),
	KILO("k", 3),
	MEGA("M", 6),
	GIGA("G", 9),
	TERA("T", 12),
	PETA("P", 15),
	EXA("E", 18),
	ZETTA("Z", 21),
	YOTTA("Y", 24);

	/**
	 * Prefix letter(s)
	 */
	final String prefix;

	/**
	 * Power of 10 representation of the base
	 *
	 * ex:
	 * 1 -> 0
	 * 10 -> 1
	 * 1 000 000 -> 6
	 * 0.000 000 001 -> -9
	 */
	final int power10;

	IsoSizePrefix(String prefix, int power10) {
		this.prefix = prefix;
		this.power10 = power10;
	}

    public String getPrefix() {
        return prefix;
    }

    public int getPower10() {
        return power10;
    }

    public String getId() {
        return name();
    }
}
