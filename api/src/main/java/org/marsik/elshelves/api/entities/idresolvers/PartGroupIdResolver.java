package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.marsik.elshelves.api.entities.PartGroupApiModel;

import java.util.UUID;

public class PartGroupIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return PartGroupApiModel.class;
	}
}
