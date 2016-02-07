package org.marsik.elshelves.backend.test.integration;

import com.jayway.restassured.http.ContentType;
import org.junit.Ignore;
import org.junit.Test;
import org.marsik.elshelves.backend.controllers.InfoController;
import org.mockito.InjectMocks;

import static com.jayway.restassured.RestAssured.given;

public class CapabilitiesTest extends BaseIntegrationTest {
    @Test
    public void testCapabilitiesEndpoint() throws Exception {
        given().
                contentType(ContentType.JSON).
                when().
                get("/info/capabilities").
                then().
                statusCode(200);
    }
}
