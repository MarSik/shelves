package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.backend.entities.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class UserToEmber implements CachingConverter<User, org.marsik.elshelves.api.entities.User, UUID> {
    @Override
    public org.marsik.elshelves.api.entities.User convert(User entity, Map<UUID, Object> cache) {
        if (entity == null) {
            return null;
        }

        if (cache.containsKey(entity.getUuid())) {
            return (org.marsik.elshelves.api.entities.User)cache.get(entity.getUuid());
        }

        org.marsik.elshelves.api.entities.User user = new org.marsik.elshelves.api.entities.User();
        user.setId(entity.getUuid());
        user.setEmail(entity.getEmail());
        user.setName(entity.getName());
        return user;
    }
}
