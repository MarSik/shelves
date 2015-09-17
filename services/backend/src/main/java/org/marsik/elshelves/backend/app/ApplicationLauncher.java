package org.marsik.elshelves.backend.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class ApplicationLauncher extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BackendApplication.class,
                ApplicationSecurity.class,
                ApplicationData.class,
                ApplicationMessaging.class,
                WebSocketConfig.class,
                ApplicationTimers.class,
                ApplicationEventBus.class,
                ApplicationHttpClients.class,
                ApplicationOauth2Authorization.class,
                ApplicationOauth2Resources.class,
                CorsConfiguration.class,
                WebFormSupportFilter.class);
    }
}
