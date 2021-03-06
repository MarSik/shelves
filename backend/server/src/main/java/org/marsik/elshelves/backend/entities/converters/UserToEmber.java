package org.marsik.elshelves.backend.entities.converters;

import org.marsik.elshelves.api.entities.AuthorizationApiModel;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.entities.Authorization;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@DependsOn("EntityToEmberConversionService")
public class UserToEmber extends AbstractEntityToEmber<User, UserApiModel> {
    @Autowired
    AuthorizationToEmber authorizationToEmber;

    @Autowired
    SourceToEmber sourceToEmber;

    @Autowired
    GroupToEmber groupToEmber;

    @Autowired
    EntityToEmberConversionService conversionService;

    public UserToEmber() {
        super(UserApiModel.class);
    }

    @PostConstruct
    void postConstruct() {
        conversionService.register(User.class, this);
    }

	@Override
	public UserApiModel convert(String path, User entity, UserApiModel user, Map<UUID, Object> cache, Set<String> include) {
		user.setEmail(entity.getEmail());
		user.setName(entity.getName());
        user.setCurrency(entity.getCurrency());

        user.setAuthorizations(new ArrayList<AuthorizationApiModel>());
        if (entity.getAuthorizations() != null) {
            for (Authorization auth : entity.getAuthorizations()) {
                user.getAuthorizations().add(authorizationToEmber.convert(path, "authorization", auth, cache, include));
            }
        }

        user.setLostAndFound(groupToEmber.convert(path, "lost-and-found", entity.getLostAndFound(), cache, include));

		return user;
	}
}
