package org.marsik.elshelves.backend.test.integration;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.marsik.elshelves.backend.app.security.ApplicationOauth2Resources;
import org.marsik.elshelves.backend.app.spring.ApplicationRest;
import org.marsik.elshelves.backend.test.config.ControllerOnlyApplication;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringApplicationConfiguration(classes = { ControllerOnlyApplication.class, ApplicationRest.class, ApplicationOauth2Resources.class })
@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest("server.port:0")
@ActiveProfiles({"test", "integration"})
public abstract class BaseIntegrationTest {
    @Value("${local.server.port}")
    private int port;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Before
    public void setupRestAssured() {
        RestAssured.port = port;
    }
}
