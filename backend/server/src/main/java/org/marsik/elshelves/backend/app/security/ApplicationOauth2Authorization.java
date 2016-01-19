package org.marsik.elshelves.backend.app.security;

import org.marsik.elshelves.backend.services.GithubOauthService;
import org.marsik.elshelves.backend.services.GoogleOauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAuthorizationServer
@ComponentScan(basePackageClasses = GoogleOauthService.class)
public class ApplicationOauth2Authorization extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private MemcacheTokenStore memcacheTokenStore;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    GoogleOauthService googleOauthService;

    @Autowired
    GithubOauthService githubOauthService;

    @Bean
    public TokenStore tokenStore() {
        return memcacheTokenStore;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient("elshelves.js")
                .authorizedGrantTypes("password", "refresh_token")
                .authorities("ROLE_USER")
                .scopes("read", "write")
                .resourceIds("elshelves")
                .secret("public");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore());
        endpoints.authenticationManager(authenticationManager);

        List<TokenGranter> tokenGranters = new ArrayList<>();
        tokenGranters.add(new GoogleTokenGranter(endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                googleOauthService
                ));
        tokenGranters.add(new GithubTokenGranter(endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                githubOauthService
        ));
        tokenGranters.add(endpoints.getTokenGranter());

        endpoints.tokenGranter(new CompositeTokenGranter(tokenGranters));
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore());
        return tokenServices;
    }
}
