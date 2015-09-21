package org.marsik.elshelves.backend.entities.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.marsik.elshelves.backend.entities.Type;

import javax.persistence.Entity;
import javax.persistence.Id;

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
	public static ShippingCalculator forValue(String s) {
		return ShippingCalculator.valueOf(s);
	}

}
