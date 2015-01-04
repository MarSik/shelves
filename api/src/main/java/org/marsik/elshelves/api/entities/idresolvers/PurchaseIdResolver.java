package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.PurchaseApiModel;

public class PurchaseIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return PurchaseApiModel.class;
	}
}
