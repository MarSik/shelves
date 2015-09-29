package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.controllers.exceptions.OperationNotPermitted;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.entities.converters.EmberToUser;
import org.marsik.elshelves.backend.entities.converters.UserToEmber;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractRestService<UserRepository, User> {
	@Autowired
	public UserService(UserRepository repository,
					   UuidGenerator uuidGenerator) {
		super(repository, uuidGenerator);
	}

	@Override
	public User create(User dto, User currentUser) throws OperationNotPermitted {
		throw new OperationNotPermitted();
	}

    @Override
    protected Iterable<User> getAllEntities(User currentUser) {
        /* TODO XXX check if the currentUser has admin rights... */

        return getRepository().findAll();
    }
}
