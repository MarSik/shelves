package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface BaseOauthService {
    String getAuthStartUrl() throws GeneralSecurityException, IOException;

    User getOrRegisterUser(User existingUser, String code, String state) throws GeneralSecurityException, IOException,
            BaseRestException;
}
