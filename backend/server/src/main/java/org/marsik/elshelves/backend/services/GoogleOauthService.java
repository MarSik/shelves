package org.marsik.elshelves.backend.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleOauthService {
    private final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private final String GOOGLE_EXTERNAL_ID = "@google.oauth.connect";

    @Value("${shelves.security.donePage}")
    private String donePage;

    @Value("${shelves.security.authPage}")
    private String authPage;

    @Value("${google.oauth2.client.id}")
    private String clientId;

    @Value("${google.oauth2.client.secret}")
    private String clientSecret;

    @Autowired
    ElshelvesUserDetailsService userDetailsService;

    @Autowired
    UserService userService;

    public GoogleAuthorizationCodeFlow getGoogleAuthFlow() throws GeneralSecurityException, IOException {
        List<String> scopes = new ArrayList<>();
        scopes.add("https://www.googleapis.com/auth/userinfo.profile");
        scopes.add("https://www.googleapis.com/auth/userinfo.email");

        final NetHttpTransport googleHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new GoogleAuthorizationCodeFlow.Builder(
                googleHttpTransport,
                new JacksonFactory(),
                clientId,
                clientSecret,
                scopes)
                .setAccessType("online")
                .build();
    }

    public User getOrRegisterUser(User currentUser, String state, String code) throws GeneralSecurityException, IOException,
            BaseRestException {
        GoogleAuthorizationCodeFlow authorizationCodeFlow = getGoogleAuthFlow();
        GoogleTokenResponse token = authorizationCodeFlow.newTokenRequest(code).setRedirectUri(authPage).execute();
        final Credential credential = authorizationCodeFlow.createAndStoreCredential(token, null);

        final NetHttpTransport googleHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final HttpRequestFactory requestFactory = googleHttpTransport.createRequestFactory(credential);

        // Make an authenticated request for user info
        final GenericUrl url = new GenericUrl(USER_INFO_URL);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        final GoogleIdentity identity = request.execute().parseAs(GoogleIdentity.class);

        if (currentUser == null) {
            currentUser = userDetailsService.createUser(identity.email, identity.id + GOOGLE_EXTERNAL_ID);

            // TODO Log in the created user
            // TODO Send a welcome email
        } else {
            currentUser.getExternalIds().add(identity.id + GOOGLE_EXTERNAL_ID);
            userService.update(currentUser, currentUser);
        }

        return currentUser;
    }

    public static class GoogleIdentity {
        String id;
        String email;
    }
}
