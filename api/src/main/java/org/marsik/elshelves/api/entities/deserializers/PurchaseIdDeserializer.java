package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.PurchaseApiModel;

public class PurchaseIdDeserializer extends EmberIdDeserializer<PurchaseApiModel> {
	@Override
	protected Class<? extends PurchaseApiModel> getEntityClass() {
		return PurchaseApiModel.class;
	}
}
