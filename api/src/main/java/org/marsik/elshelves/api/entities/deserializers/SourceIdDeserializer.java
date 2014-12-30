package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.SourceApiModel;

public class SourceIdDeserializer extends EmberIdDeserializer<SourceApiModel> {
	@Override
	protected Class<? extends SourceApiModel> getEntityClass() {
		return SourceApiModel.class;
	}
}
