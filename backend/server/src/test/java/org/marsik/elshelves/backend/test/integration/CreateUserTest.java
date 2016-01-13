package org.marsik.elshelves.backend.test.integration;

import com.jayway.restassured.http.ContentType;
import org.junit.Test;
import org.marsik.elshelves.api.entities.UserApiModel;

import static com.jayway.restassured.RestAssured.given;

public class CreateUserTest extends BaseWebTest {
    @Test
    public void testSimpleUserCreation() throws Exception {
        UserApiModel user = new UserApiModel();
        user.setName("Test user");
        user.setEmail("test@user.com");

        given().
                contentType(ContentType.JSON).
                body(user).
        when().
                post("/v1/users").
        then().
                statusCode(200);
    }
}
