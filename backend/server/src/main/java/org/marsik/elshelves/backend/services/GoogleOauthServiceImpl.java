package org.marsik.elshelves.backend.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.GoogleIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleOauthServiceImpl implements GoogleOauthService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleOauthService.class);

    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final String GOOGLE_EXTERNAL_ID = "@google.oauth.connect";

    @Value("${google.oauth2.donePage}")
    private String donePage;

    @Value("${google.oauth2.client.id}")
    private String clientId;

    @Value("${google.oauth2.client.secret}")
    private String clientSecret;

    @Autowired
    ElshelvesUserDetailsService userDetailsService;

    @Autowired
    UserService userService;

    @Autowired
    MappingJackson2HttpMessageConverter messageConverter;

    static final JacksonFactory jsonFactory = new JacksonFactory();

    @Override
    public GoogleAuthorizationCodeFlow getGoogleAuthFlow() throws GeneralSecurityException, IOException {
        List<String> scopes = new ArrayList<>();
        scopes.add("https://www.googleapis.com/auth/userinfo.profile");
        scopes.add("https://www.googleapis.com/auth/userinfo.email");

        final NetHttpTransport googleHttpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new GoogleAuthorizationCodeFlow.Builder(
                googleHttpTransport,
                jsonFactory,
                clientId,
                clientSecret,
                scopes)
                .setAccessType("online")
                .build();
    }

    @Override
    public User getOrRegisterUser(User currentUser, String state, String code) throws GeneralSecurityException, IOException,
            BaseRestException {
        GoogleAuthorizationCodeFlow authorizationCodeFlow = getGoogleAuthFlow();
        GoogleTokenResponse token = authorizationCodeFlow.newTokenRequest(code).setRedirectUri(donePage).execute();
        final Credential credential = authorizationCodeFlow.createAndStoreCredential(token, null);

        final NetHttpTransport googleHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final HttpRequestFactory requestFactory = googleHttpTransport.createRequestFactory(credential);

        // Make an authenticated request for user info
        final GenericUrl url = new GenericUrl(USER_INFO_URL);
        final HttpRequest request = requestFactory.buildGetRequest(url);
        request.getHeaders().setContentType("application/json");
        final HttpResponse response = request.execute();
        final String responseData = response.parseAsString();

        GoogleIdentity identity = messageConverter.getObjectMapper().readerFor(GoogleIdentity.class).readValue(responseData);

        if (currentUser == null) {
            if (identity.getVerifiedEmail()) {
                currentUser = userDetailsService.createOrAttachUser(identity.getEmail(),
                        identity.getId() + GOOGLE_EXTERNAL_ID);
            } else {
                return null;
            }
        } else {
            currentUser.getExternalIds().add(identity.getId() + GOOGLE_EXTERNAL_ID);
            userService.update(currentUser, currentUser);
        }

        return currentUser;
    }

}
