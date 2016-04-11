package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.User;

import java.io.IOException;

/**
 * Created by msivak on 11.4.16.
 */
public interface GithubOauthService extends BaseOauthService {
    @Override
    String getAuthStartUrl() throws IOException;

    @Override
    User getOrRegisterUser(User existingUser, String code, String state);

    @Override
    boolean isConfigured();
}
