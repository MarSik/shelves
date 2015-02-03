package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.DocumentApiModel;

public class DocumentIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return DocumentApiModel.class;
	}
}
