package org.marsik.elshelves.backend.services;

import java.util.UUID;

import org.marsik.elshelves.backend.dtos.UserInfo;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UserRepository;


public interface UserService extends AbstractRestServiceIntf<UserRepository, User> {
    UserInfo getInfo(UUID id, User currentUser);
}
