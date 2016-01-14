package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UserRepository;


public interface UserService extends AbstractRestServiceIntf<UserRepository, User> {
}
