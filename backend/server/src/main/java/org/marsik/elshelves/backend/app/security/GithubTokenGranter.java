package org.marsik.elshelves.backend.app.security;

import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.SimpleAuthority;
import org.marsik.elshelves.backend.services.GithubOauthService;
import org.marsik.elshelves.backend.services.GoogleOauthService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class GithubTokenGranter implements TokenGranter {
    AuthorizationServerTokenServices tokenServices;
    ClientDetailsService clientDetailsService;
    OAuth2RequestFactory requestFactory;
    GithubOauthService githubOauthService;

    private final static String GRANT_TYPE = "github";

    public GithubTokenGranter(AuthorizationServerTokenServices tokenServices,
                              ClientDetailsService clientDetailsService,
                              OAuth2RequestFactory requestFactory,
                              GithubOauthService githubOauthService) {
        this.tokenServices = tokenServices;
        this.clientDetailsService = clientDetailsService;
        this.requestFactory = requestFactory;
        this.githubOauthService = githubOauthService;
    }

    @Override
    public OAuth2AccessToken grant(String s, TokenRequest tokenRequest) {
        if (!tokenRequest.getGrantType().equals(GRANT_TYPE)) {
            return null;
        }

        // Get user details and prepare client id
        User user = githubOauthService.getUserEmail(
                tokenRequest.getRequestParameters().get("code"),
                tokenRequest.getRequestParameters().get("state"));

        if (user == null) {
            return null;
        }

        ClientDetails client = clientDetailsService.loadClientByClientId(tokenRequest.getClientId());
        OAuth2Request request = requestFactory.createOAuth2Request(client, tokenRequest);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleAuthority("ROLE_FEDERATED"));
        authorities.add(new SimpleAuthority("ROLE_USER"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId().toString(), "N/A", authorities);
        return tokenServices.createAccessToken(new OAuth2Authentication(request, authentication));
    }

}
