package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.CodeApiModel;

public class CodeIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return CodeApiModel.class;
	}
}
