package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.UnitApiModel;

public class UnitIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return UnitApiModel.class;
	}
}
