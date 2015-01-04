package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.marsik.elshelves.api.entities.FootprintApiModel;

import java.util.UUID;

public class FootprintIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return FootprintApiModel.class;
	}
}
