package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.FootprintApiModel;

public class FootprintIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return FootprintApiModel.class;
	}
}
