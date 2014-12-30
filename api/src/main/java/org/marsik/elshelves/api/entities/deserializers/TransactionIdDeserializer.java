package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.TransactionApiModel;

public class TransactionIdDeserializer extends EmberIdDeserializer<TransactionApiModel> {
	@Override
	protected Class<? extends TransactionApiModel> getEntityClass() {
		return TransactionApiModel.class;
	}
}
