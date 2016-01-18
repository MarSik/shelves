package org.marsik.elshelves.backend.controllers;

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
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.ElshelvesUserDetailsService;
import org.marsik.elshelves.backend.services.GoogleOauthService;
import org.marsik.elshelves.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/federated")
public class FederatedLogin {
    @Value("${shelves.security.donePage}")
    private String donePage;

    @Value("${shelves.security.authPage}")
    private String authPage;

    @Autowired
    GoogleOauthService googleOauthService;


    @RequestMapping("/google/done")
    public void googleLoginDone(
            @CurrentUser User currentUser,
            HttpServletResponse response,
            @RequestParam("code") String code,
            @RequestParam("state") String state) throws IOException, GeneralSecurityException, BaseRestException {


        // TODO Compute login command for the Ember app that instructs it to perform a oauth request with
        // google type grant

        response.sendRedirect(donePage);
    }

    @RequestMapping("/google/login")
    public void googleLoginStart(HttpServletResponse response) throws IOException, GeneralSecurityException {
        SecureRandom sr1 = new SecureRandom();
        String stateToken = "google;" + sr1.nextInt();

        GoogleAuthorizationCodeFlow authorizationCodeFlow = googleOauthService.getGoogleAuthFlow();

        String url = authorizationCodeFlow.newAuthorizationUrl().setState(stateToken).setRedirectUri(authPage).build();
        response.sendRedirect(url);
    }
}
