package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EmberToUser extends AbstractEmberToEntity<UserApiModel, User> {
	public EmberToUser() {
		super(User.class);
	}

	@Autowired
	EmberToSource emberToSource;

	@Override
	public User convert(UserApiModel dto, User u, int nested, Map<UUID, Object> cache) {
		u.setEmail(dto.getEmail());
		u.setName(dto.getName());
		u.setPassword(dto.getPassword());
		u.setId(dto.getId());

		u.setAuthorizations(new IdentifiedEntity.UnprovidedSet<>());

		if (dto.getProjectSource() != null) {
			u.setProjectSource(emberToSource.convert(dto.getProjectSource(), nested, cache));
		}

		return u;
	}
}
