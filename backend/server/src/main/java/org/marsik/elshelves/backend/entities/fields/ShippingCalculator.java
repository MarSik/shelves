package org.marsik.elshelves.backend.entities.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import org.marsik.elshelves.backend.entities.Type;

public enum ShippingCalculator {
	NONE(null),
	FARNELL(null);

	interface Shipping {
		float getShipping(Iterable<Type> types);
	}

	final Shipping shipping;

	public float getShipping(Iterable<Type> types) {
		return 0f;
	}

	ShippingCalculator(Shipping shipping) {
		this.shipping = shipping;
	}

	public String getId() {
		return name();
	}

	@JsonCreator
	public static ShippingCalculator forValue(JsonNode s) {
		if (s.isObject()) {
			return s.get("id") == null ? NONE : ShippingCalculator.valueOf(s.get("id").asText());
		} else if (s.isTextual()) {
			return ShippingCalculator.valueOf(s.asText());
		} else if (s.isNull()) {
			return null;
		} else if (s.isNumber()) {
			return ShippingCalculator.values()[s.asInt()];
		} else {
			return null;
		}
	}
}
