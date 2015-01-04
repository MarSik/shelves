package org.marsik.elshelves.api.entities.idresolvers;

import org.marsik.elshelves.api.entities.UserApiModel;

public class UserIdResolver extends AbstractIdResolver {
	@Override
	protected Class<?> getType() {
		return UserApiModel.class;
	}
}
