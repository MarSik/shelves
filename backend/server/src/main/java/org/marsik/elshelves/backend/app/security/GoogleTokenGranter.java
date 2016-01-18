package org.marsik.elshelves.backend.app.security;

import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.services.GoogleOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class GoogleTokenGranter implements TokenGranter {
    @Autowired
    AuthorizationServerTokenServices tokenServices;

    @Autowired
    OAuth2RequestFactory requestFactory;

    @Autowired
    GoogleOauthService googleOauthService;

    private final static String GRANT_TYPE = "google";

    public GoogleTokenGranter(ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory) {

    }

    @Override
    public OAuth2AccessToken grant(String s, TokenRequest tokenRequest) {
        if (!tokenRequest.getGrantType().equals(GRANT_TYPE)) {
            throw new InvalidClientException("Unauthorized grant type: " + tokenRequest.getGrantType());
        }

        // TODO get user details and prepare client id
        try {
            User user = googleOauthService.getOrRegisterUser(null,
                    tokenRequest.getRequestParameters().get("state"),
                    tokenRequest.getRequestParameters().get("code"));

            ClientDetails client = null;

            OAuth2Request request = requestFactory.createOAuth2Request(client, tokenRequest);
            return tokenServices.createAccessToken(new OAuth2Authentication(request, null));

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BaseRestException e) {
            e.printStackTrace();
        }

        return null;
    }
}
