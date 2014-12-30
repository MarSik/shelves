package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.BoxApiModel;

public class BoxIdDeserializer extends EmberIdDeserializer<BoxApiModel> {
	@Override
	protected Class<? extends BoxApiModel> getEntityClass() {
		return BoxApiModel.class;
	}
}
