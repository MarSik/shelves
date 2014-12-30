package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.PartGroupApiModel;

public class PartGroupIdDeserializer extends EmberIdDeserializer<PartGroupApiModel> {
	@Override
	protected Class<? extends PartGroupApiModel> getEntityClass() {
		return PartGroupApiModel.class;
	}
}
