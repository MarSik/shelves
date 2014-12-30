package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.FootprintApiModel;

public class FootprintIdDeserializer extends EmberIdDeserializer<FootprintApiModel> {
	@Override
	protected Class<? extends FootprintApiModel> getEntityClass() {
		return FootprintApiModel.class;
	}
}
