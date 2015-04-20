package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.AbstractEntityApiModel;
import org.marsik.elshelves.api.entities.UserApiModel;

public class UserIdResolver extends AbstractIdResolver {
	@Override
	protected Class<? extends AbstractEntityApiModel> getType() {
		return UserApiModel.class;
	}
}
