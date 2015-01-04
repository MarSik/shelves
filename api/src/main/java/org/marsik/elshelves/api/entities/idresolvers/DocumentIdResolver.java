package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.marsik.elshelves.api.entities.DocumentApiModel;

import java.util.UUID;

public class DocumentIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return DocumentApiModel.class;
	}
}
