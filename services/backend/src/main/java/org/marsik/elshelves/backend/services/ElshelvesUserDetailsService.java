package org.marsik.elshelves.backend.services;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface ElshelvesUserDetailsService extends UserDetailsService {
    String createUser(org.marsik.elshelves.api.entities.User userInfo);
    String verifyUser(String code);
}
