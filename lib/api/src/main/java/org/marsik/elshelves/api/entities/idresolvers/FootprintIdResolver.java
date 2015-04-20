package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.FootprintApiModel;

public class FootprintIdResolver extends AbstractIdResolver {
	@Override
	protected Class<? extends AbstractEntityApiModel> getType() {
		return FootprintApiModel.class;
	}
}
