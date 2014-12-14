package org.marsik.elshelves.backend.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
                .anonymous().and()
                .authorizeRequests()
                .antMatchers("/status", "/favicon.ico").permitAll()
                .antMatchers("/mail/**").permitAll()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.POST, "/users/verify/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/test/**").permitAll()
                .anyRequest().authenticated();
    }
}
