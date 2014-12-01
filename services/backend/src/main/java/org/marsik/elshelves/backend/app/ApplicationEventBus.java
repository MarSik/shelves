package org.marsik.elshelves.backend.app;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import org.marsik.elshelves.backend.app.spring.EventBusPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationEventBus {
    @Bean
    MBassador<Object> eventBus() {
        return new MBassador<>(BusConfiguration.SyncAsync());
    }

    @Bean
    EventBusPostProcessor eventBusPostProcessor() {
        return new EventBusPostProcessor();
    }
}
