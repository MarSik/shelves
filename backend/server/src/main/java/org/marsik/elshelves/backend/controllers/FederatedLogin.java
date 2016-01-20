package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.controllers.exceptions.BaseRestException;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.security.CurrentUser;
import org.marsik.elshelves.backend.services.GithubOauthService;
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
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/federated")
public class FederatedLogin {
    @Value("${shelves.security.donePage}")
    private String donePage;

    @Autowired
    GoogleOauthService googleOauthService;

    @Autowired
    MappingJackson2HttpMessageConverter converter;

    @Autowired
    GithubOauthService githubOauthService;

    private void genericOauthDoneRedirect(HttpServletResponse response,
            String grantType, String code, String state) throws IOException {
        // Compute login command for the Ember app that instructs it to perform a oauth request with
        // google type grant
        Map<String, String> authFormula = new THashMap<>();
        authFormula.put("grant_type", grantType);
        authFormula.put("code", code);
        authFormula.put("state", state);

        final String jsonAuthFormula = converter.getObjectMapper().writeValueAsString(authFormula);
        response.sendRedirect(
                donePage + "?auth=" + Base64.getEncoder().encodeToString(jsonAuthFormula.getBytes("UTF8")));
    }

    // Google federated login

    @RequestMapping("/google/login")
    public void googleLoginStart(HttpServletResponse response) throws IOException, GeneralSecurityException {
        response.sendRedirect(googleOauthService.getAuthStartUrl());
    }

    @RequestMapping("/google/done")
    public void googleLoginDone(
            @CurrentUser User currentUser,
            HttpServletResponse response,
            @RequestParam("code") String code,
            @RequestParam("state") String state) throws IOException, GeneralSecurityException, BaseRestException {
        genericOauthDoneRedirect(response, "google", code, state);
    }

    // Github federated login

    @RequestMapping("/github/login")
    public void githubLoginStart(HttpServletResponse response) throws IOException, GeneralSecurityException {
        response.sendRedirect(githubOauthService.getAuthStartUrl());
    }

    @RequestMapping("/github/done")
    public void githubLoginDone(
            @CurrentUser User currentUser,
            HttpServletResponse response,
            @RequestParam("code") String code,
            @RequestParam("state") String state) throws IOException, GeneralSecurityException, BaseRestException {
        genericOauthDoneRedirect(response, "github", code, state);
    }
}
