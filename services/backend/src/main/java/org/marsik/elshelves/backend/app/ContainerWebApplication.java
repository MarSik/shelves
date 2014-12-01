package org.marsik.elshelves.backend.app;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class ContainerWebApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BackendApplication.class,
                ApplicationSecurity.class,
                ApplicationData.class,
                ApplicationMessaging.class,
                WebSocketConfig.class,
                ApplicationTimers.class);
    }
}
