package org.marsik.elshelves.backend.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ApplicationOauth2Resources extends ResourceServerConfigurerAdapter {
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
                .authorizeRequests()
                    // Anybody can try to authenticate
                    .antMatchers("/oauth/token").permitAll()

                    // Status and website icon are open
                    .antMatchers("/status", "/favicon.ico").permitAll()
                    .antMatchers("/hystrix.stream").permitAll()

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
