package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.backend.entities.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToUser implements CachingConverter<org.marsik.elshelves.api.entities.User, User, UUID> {
    @Override
    public User convert(org.marsik.elshelves.api.entities.User dto, Map<UUID, Object> cache) {
        if (dto == null) {
            return null;
        }

        if (cache.containsKey(dto.getId())) {
            return (User)cache.get(dto.getId());
        }

        User u = new User();
        u.setEmail(dto.getEmail());
        u.setName(dto.getName());
        u.setPassword(dto.getPassword());
        u.setUuid(dto.getId());
        return u;
    }
}
