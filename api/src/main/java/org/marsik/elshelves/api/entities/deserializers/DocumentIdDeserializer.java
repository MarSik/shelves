package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.DocumentApiModel;

public class DocumentIdDeserializer extends EmberIdDeserializer<DocumentApiModel> {
	@Override
	protected Class<? extends DocumentApiModel> getEntityClass() {
		return DocumentApiModel.class;
	}
}
