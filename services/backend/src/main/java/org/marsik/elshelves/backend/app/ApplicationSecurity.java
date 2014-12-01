package org.marsik.elshelves.backend.app;

import org.marsik.elshelves.backend.configuration.MailgunConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurity extends
        WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public MailgunConfiguration getMAilgunConfiguration() {
        return new MailgunConfiguration();
    }

    @Bean
    AuthenticationSuccessHandler getSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler h = new SavedRequestAwareAuthenticationSuccessHandler();
        h.setTargetUrlParameter("return-to");
        return h;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login")
                .failureUrl("/login?failed")
                .passwordParameter("j_password")
                .usernameParameter("j_username")
                .defaultSuccessUrl("/d")
                .successHandler(getSuccessHandler())
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and()
            .authorizeRequests()
                .antMatchers("/", "/setup", "/status", "/favicon.ico").permitAll()
                .antMatchers("/mail/**").permitAll()
                .anyRequest().authenticated();

        http.csrf().requireCsrfProtectionMatcher(new CsrfMatcher());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    static class CsrfMatcher implements RequestMatcher {
        private Pattern allowedMethods = Pattern.compile("^(HEAD|TRACE|OPTIONS)$");
        private Pattern allowedGet = Pattern.compile("^(GET)$");
        private RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/mail/.*", null);

        @Override
        public boolean matches(HttpServletRequest request) {
            // No CSRF due to allowedMethod
            if(allowedMethods.matcher(request.getMethod()).matches())
                return false;

            // No CSRF due to allowedGet
            if(allowedGet.matcher(request.getMethod()).matches())
                return false;

            // No CSRF due to api call
            if(apiMatcher.matches(request))
                return false;

            // CSRF for everything else
            return true;
        }
    }
}
