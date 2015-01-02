package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.RequirementApiModel;

public class RequirementIdDeserializer extends EmberIdDeserializer<RequirementApiModel> {
	@Override
	protected Class<? extends RequirementApiModel> getEntityClass() {
		return RequirementApiModel.class;
	}
}
