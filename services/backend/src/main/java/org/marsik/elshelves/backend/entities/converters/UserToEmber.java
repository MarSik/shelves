package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class UserToEmber implements CachingConverter<User, UserApiModel, UUID> {
    @Override
    public UserApiModel convert(User entity, int nested, Map<UUID, Object> cache) {
        if (entity == null) {
            return null;
        }

        if (cache.containsKey(entity.getUuid())) {
            return (UserApiModel)cache.get(entity.getUuid());
        }

        UserApiModel user = new UserApiModel();
		if (nested > 0) {
			cache.put(entity.getUuid(), user);
		}

		return convert(entity, user, nested, cache);
    }

	@Override
	public UserApiModel convert(User entity, UserApiModel user, int nested, Map<UUID, Object> cache) {
		user.setId(entity.getUuid());
		user.setEmail(entity.getEmail());
		user.setName(entity.getName());
		return user;
	}
}
