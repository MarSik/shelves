package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.LotApiModel;

public class LotIdDeserializer extends EmberIdDeserializer<LotApiModel> {
	@Override
	protected Class<? extends LotApiModel> getEntityClass() {
		return LotApiModel.class;
	}
}
