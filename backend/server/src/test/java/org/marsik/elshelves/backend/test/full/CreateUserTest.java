package org.marsik.elshelves.backend.test.full;

import io.restassured.http.ContentType;
import org.junit.Test;
import org.marsik.elshelves.api.entities.UserApiModel;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public class CreateUserTest extends BaseFullIntegrationTest {
    @Autowired
    UserRepository userRepository;

    @Test
    public void testSimpleUserCreation() throws Exception {
        UserApiModel user = new UserApiModel();
        user.setName("Test user");
        user.setEmail("test@user.test");

        final User oldUser = userRepository.getUserByEmail(user.getEmail());
        if (oldUser != null) {
            userRepository.delete(oldUser);
            userRepository.flush();
        }

        given().
                contentType(ContentType.JSON).
                body(user).
        when().
                post("/v1/users").
        then().
                statusCode(200);
    }
}
