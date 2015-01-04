package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.TransactionApiModel;

public class TransactionIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return TransactionApiModel.class;
	}
}
