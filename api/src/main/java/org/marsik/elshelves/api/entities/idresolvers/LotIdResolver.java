package org.marsik.elshelves.api.entities.idresolvers;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.marsik.elshelves.api.entities.LotApiModel;

import java.util.UUID;

public class LotIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return LotApiModel.class;
	}
}
