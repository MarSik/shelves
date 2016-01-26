package org.marsik.elshelves.backend.controllers;

import gnu.trove.map.hash.THashMap;
import org.marsik.elshelves.backend.services.GithubOauthService;
import org.marsik.elshelves.backend.services.GoogleOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/info")
public class InfoController {
    @Autowired
    GoogleOauthService googleOauthService;

    @Autowired
    GithubOauthService githubOauthService;

    @RequestMapping("/capabilities")
    public Map<String, Object> getCapabilities() {
        Map<String, Object> capa = new THashMap<>();
        capa.put("google", googleOauthService.isConfigured());
        capa.put("github", githubOauthService.isConfigured());
        return capa;
    }
}
