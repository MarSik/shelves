package org.marsik.elshelves.backend.app;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Hystrix.
 *
 * @author Lieven Doclo
 */
@ConfigurationProperties(prefix = "hystrix", ignoreUnknownFields = true)
@SuppressFBWarnings("URF_UNREAD_FIELD")
class HystrixProperties {
    boolean enabled = true;
    boolean streamEnabled = false;
    String streamUrl = "/hystrix.stream";
}
