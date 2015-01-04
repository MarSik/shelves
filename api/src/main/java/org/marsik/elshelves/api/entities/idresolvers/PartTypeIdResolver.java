package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.PartTypeApiModel;

public class PartTypeIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return PartTypeApiModel.class;
	}
}
