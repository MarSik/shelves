package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ElshelvesUserDetailsService extends UserDetailsService {
    String createUser(UserApiModel userInfo);
    String verifyUser(String code);
    User getUser(String email);
}
