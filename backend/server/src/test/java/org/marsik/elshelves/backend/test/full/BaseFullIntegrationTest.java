package org.marsik.elshelves.backend.test.full;

import io.restassured.RestAssured;
import com.netflix.hystrix.Hystrix;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.marsik.elshelves.ApplicationLauncher;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@WebAppConfiguration
@SpringApplicationConfiguration(classes = { ApplicationLauncher.class })
@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest("server.port:0")
@ActiveProfiles({"test", "integration"})
public abstract class BaseFullIntegrationTest {

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

    @Before
    public void resetHystrix() {
        Hystrix.reset();
    }
}
