package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.PartTypeApiModel;

public class PartTypeIdDeserializer extends EmberIdDeserializer<PartTypeApiModel> {
	@Override
	protected Class<? extends PartTypeApiModel> getEntityClass() {
		return PartTypeApiModel.class;
	}
}
