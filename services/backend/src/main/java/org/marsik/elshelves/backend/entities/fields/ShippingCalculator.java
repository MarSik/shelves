package org.marsik.elshelves.backend.entities.fields;

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
}
