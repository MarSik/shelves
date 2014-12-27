package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToUser implements CachingConverter<UserApiModel, User, UUID> {
    @Override
    public User convert(UserApiModel dto, int nested, Map<UUID, Object> cache) {
        if (dto == null) {
            return null;
        }

        if (cache.containsKey(dto.getId())) {
            return (User)cache.get(dto.getId());
        }

        User u = new User();
		if (nested > 0) {
			cache.put(dto.getId(), u);
		}
		return convert(dto, u, nested, cache);
    }

	@Override
	public User convert(UserApiModel dto, User u, int nested, Map<UUID, Object> cache) {
		u.setEmail(dto.getEmail());
		u.setName(dto.getName());
		u.setPassword(dto.getPassword());
		u.setUuid(dto.getId());
		return u;
	}
}
