package org.marsik.elshelves.api.entities.deserializers;

import org.marsik.elshelves.api.entities.UserApiModel;

public class UserIdDeserializer extends EmberIdDeserializer<UserApiModel> {
	@Override
	protected Class<? extends UserApiModel> getEntityClass() {
		return UserApiModel.class;
	}
}
