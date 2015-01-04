package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.marsik.elshelves.api.entities.BoxApiModel;

import java.util.UUID;

public class BoxIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return BoxApiModel.class;
	}
}
