package org.marsik.elshelves.backend.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Hystrix.
 *
 * @author Lieven Doclo
 */
@ConfigurationProperties(prefix = "hystrix", ignoreUnknownFields = true)
class HystrixProperties {
    boolean enabled = true;
    boolean streamEnabled = false;
    String streamUrl = "/hystrix.stream";
}
