package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.RequirementApiModel;

public class RequirementIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return RequirementApiModel.class;
	}
}
