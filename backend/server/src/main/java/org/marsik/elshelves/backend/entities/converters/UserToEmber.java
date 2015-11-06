package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Service
public class UserToEmber extends AbstractEntityToEmber<User, UserApiModel> {
    @Autowired
    AuthorizationToEmber authorizationToEmber;

    @Autowired
    SourceToEmber sourceToEmber;

    public UserToEmber() {
        super(UserApiModel.class);
    }

	@Override
	public UserApiModel convert(User entity, UserApiModel user, int nested, Map<UUID, Object> cache) {
		user.setId(entity.getId());

		if (nested == 0) {
			return user;
		}

		user.setEmail(entity.getEmail());
		user.setName(entity.getName());

        user.setAuthorizations(new ArrayList<AuthorizationApiModel>());
        if (entity.getAuthorizations() != null) {
            for (Authorization auth : entity.getAuthorizations()) {
                user.getAuthorizations().add(authorizationToEmber.convert(auth, nested - 1, cache));
            }
        }

        user.setProjectSource(sourceToEmber.convert(entity.getProjectSource(), nested - 1, cache));

		return user;
	}
}
