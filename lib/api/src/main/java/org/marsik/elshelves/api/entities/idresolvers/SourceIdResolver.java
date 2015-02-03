package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.SourceApiModel;

public class SourceIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return SourceApiModel.class;
	}
}
