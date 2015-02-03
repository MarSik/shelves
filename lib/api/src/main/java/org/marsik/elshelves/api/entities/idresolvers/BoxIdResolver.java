package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.BoxApiModel;

public class BoxIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return BoxApiModel.class;
	}
}
