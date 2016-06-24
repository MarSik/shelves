package org.marsik.elshelves.backend.test.full;

import static io.restassured.RestAssured.given;

import org.junit.Test;

public class CorsOauthTest extends BaseFullIntegrationTest {
    @Test
    public void testOauthOptions() throws Exception {
        given().
                header("Access-Control-Request-Method", "POST").
                header("Access-Control-Request-Headers", "origin, x-requested-with, accept").
                header("Origin", "https://test.domain.somewhere").
        when()
                .options("/oauth/token")
                .then()
                .statusCode(200);
    }
}
