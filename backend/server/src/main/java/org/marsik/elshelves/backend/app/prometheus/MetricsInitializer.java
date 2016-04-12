package org.marsik.elshelves.backend.app.prometheus;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.marsik.elshelves.backend.repositories.LotRepository;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsInitializer implements ServletContextInitializer {
    @Autowired
    LotRepository lotRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("metrics", new MetricsServlet());
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/metrics");

        DefaultExports.initialize();

        ShelvesStatsCollector shelvesStatsCollector = new ShelvesStatsCollector(lotRepository, userRepository);
        shelvesStatsCollector.register();
    }
}
