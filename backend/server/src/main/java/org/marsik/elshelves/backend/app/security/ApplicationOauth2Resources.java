package org.marsik.elshelves.backend.app.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableResourceServer
public class ApplicationOauth2Resources extends ResourceServerConfigurerAdapter {
    @Value("${shelves.security.loginPage}")
    String loginPage;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources
                .resourceId("elshelves");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
				// Enable anonymous access
				.anonymous()
				.and()

				// Disable sessions
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // Configure the default landing page (the default REST app talking to this server)
                .formLogin()
                .loginPage(loginPage)
                .and()

                .authorizeRequests()
                    // Anybody can try to authenticate
                    .antMatchers("/oauth/token").permitAll()
                    .antMatchers("/federated/**").permitAll()

                    // Status and website icon are open
                    .antMatchers("/status", "/favicon.ico").permitAll()
                    .antMatchers("/info/**").permitAll()
                    .antMatchers("/hystrix.stream").permitAll()
                    .antMatchers("/metric").permitAll()

                    // Mail api is verified using HMAC
                    .antMatchers("/*/mail/**").permitAll()

                    // Schema is needed for clients to start properly
                    .antMatchers("/*/schema").permitAll()

                    // User registration is open
                    .antMatchers(HttpMethod.POST, "/v1/users").permitAll()

                    // Verification of registration emails is open
                    .antMatchers(HttpMethod.POST, "/v1/users/verify/**").permitAll()
				    .antMatchers(HttpMethod.POST, "/v1/users/reverify/**").permitAll()

                    // OPTIONS calls must be open for CORS to work properly
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // Test endpoints are open
                    .antMatchers("/test/**").permitAll()

                    // The rest of the API requires valid token
                    .anyRequest().hasAuthority("ROLE_USER");
    }
}
