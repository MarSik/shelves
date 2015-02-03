package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.PartGroupApiModel;

public class PartGroupIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return PartGroupApiModel.class;
	}
}
