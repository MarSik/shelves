package org.marsik.elshelves.backend.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.GoogleOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/federated")
public class FederatedLogin {
    @Value("${shelves.security.donePage}")
    private String donePage;

    @Value("${shelves.security.authPage}")
    private String authPage;

    @Autowired
    GoogleOauthService googleOauthService;

    @Autowired
    MappingJackson2HttpMessageConverter converter;

    @RequestMapping("/google/done")
    public void googleLoginDone(
            @CurrentUser User currentUser,
            HttpServletResponse response,
            @RequestParam("code") String code,
            @RequestParam("state") String state) throws IOException, GeneralSecurityException, BaseRestException {


        // Compute login command for the Ember app that instructs it to perform a oauth request with
        // google type grant
        Map<String, String> authFormula = new THashMap<>();
        authFormula.put("grant_type", "google");
        authFormula.put("code", code);
        authFormula.put("state", state);

        final String jsonAuthFormula = converter.getObjectMapper().writeValueAsString(authFormula);
        response.sendRedirect(donePage + "?auth=" + Base64.getEncoder().encodeToString(jsonAuthFormula.getBytes("UTF8")));
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
