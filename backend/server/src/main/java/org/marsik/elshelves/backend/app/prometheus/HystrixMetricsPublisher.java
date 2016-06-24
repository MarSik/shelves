package org.marsik.elshelves.backend.app.prometheus;

import com.soundcloud.prometheus.hystrix.HystrixPrometheusMetricsPublisher;
import io.prometheus.client.CollectorRegistry;

public class HystrixMetricsPublisher extends HystrixPrometheusMetricsPublisher {
    public HystrixMetricsPublisher() {
        super("shelves_backend", CollectorRegistry.defaultRegistry, true);
    }
}
