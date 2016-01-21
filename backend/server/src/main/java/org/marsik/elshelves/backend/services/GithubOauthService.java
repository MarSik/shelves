package org.marsik.elshelves.backend.services;

import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.GithubIdentity;
import org.marsik.elshelves.backend.security.GithubTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;

@Service
public class GithubOauthService implements BaseOauthService {
    private static final Logger logger = LoggerFactory.getLogger(GithubOauthService.class);

    private static final String AUTH_START_URL = "https://github.com/login/oauth/authorize";
    private static final String AUTH_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String USER_INFO = "https://api.github.com/user";

    private static final String GITHUB_EXTERNAL_ID = "@github.oauth";

    @Value("${github.oauth2.client.id}")
    private String clientId;

    @Value("${github.oauth2.client.secret}")
    private String clientSecret;

    @Autowired
    RestTemplate rest;

    @Autowired
    ElshelvesUserDetailsService userDetailsService;

    @Override
    public String getAuthStartUrl() throws IOException {
        return AUTH_START_URL
                + "?client_id="
                + clientId
                + "&scope=user:email&state="
                + URLEncoder.encode(userDetailsService.startExternalLoginRequest("github"), "UTF-8");
    }

    @Override
    public User getOrRegisterUser(User existingUser, String code, String state) {
        if (!userDetailsService.verifyExternalLoginRequest(state)) {
            return null;
        }

        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("client_id", clientId);
        request.add("client_secret", clientSecret);
        request.add("code", code);
        request.add("state", state);

        GithubTokenResponse token = rest.postForObject(AUTH_ACCESS_TOKEN_URL, request, GithubTokenResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "token " + token.getAccessToken());

        HttpEntity<String> entity = new HttpEntity<String>(headers);

        ResponseEntity<GithubIdentity> response = rest.exchange(USER_INFO, HttpMethod.GET, entity, GithubIdentity.class);

        if (existingUser == null) {
            existingUser = userDetailsService.createOrAttachUser(
                    response.getBody().getLogin(),
                    response.getBody().getEmail(),
                    response.getBody().getLogin() + GITHUB_EXTERNAL_ID);
        } else {
            existingUser = userDetailsService.attachUser(existingUser, response.getBody().getLogin() + GITHUB_EXTERNAL_ID);
        }

        return existingUser;
    }
}
