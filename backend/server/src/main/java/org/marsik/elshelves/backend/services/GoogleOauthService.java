package org.marsik.elshelves.backend.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleOauthService {
    GoogleAuthorizationCodeFlow getGoogleAuthFlow() throws GeneralSecurityException, IOException;

    User getOrRegisterUser(User currentUser, String state, String code) throws GeneralSecurityException, IOException,
            BaseRestException;
}
