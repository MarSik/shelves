package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.LotApiModel;

public class LotIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return LotApiModel.class;
	}
}
