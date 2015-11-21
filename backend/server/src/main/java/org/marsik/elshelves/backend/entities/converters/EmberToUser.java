package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.entities.IdentifiedEntity;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class EmberToUser extends AbstractEmberToEntity<UserApiModel, User> {
	public EmberToUser() {
		super(User.class);
	}

	@Autowired
	EmberToSource emberToSource;

	@Autowired
	EmberToGroup emberToGroup;

	@Override
	public User convert(String path, UserApiModel dto, User u, Map<UUID, Object> cache, Set<String> include) {
		u.setEmail(dto.getEmail());
		u.setName(dto.getName());
		u.setPassword(dto.getPassword());
		u.setId(dto.getId());
		u.setCurrency(dto.getCurrency());

		u.setAuthorizations(new IdentifiedEntity.UnprovidedSet<>());

		if (dto.getProjectSource() != null) {
			u.setProjectSource(emberToSource.convert(path, "project-source", dto.getProjectSource(), cache, include));
		}

		if (dto.getLostAndFound() != null) {
			u.setLostAndFound(emberToGroup.convert(path, "lost-and-found", dto.getLostAndFound(), cache, include));
		}

		return u;
	}
}
