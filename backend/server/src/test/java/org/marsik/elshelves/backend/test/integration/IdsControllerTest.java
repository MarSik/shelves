package org.marsik.elshelves.backend.test.integration;

import io.restassured.http.ContentType;
import org.junit.Ignore;
import org.junit.Test;
import org.marsik.elshelves.backend.app.security.MemcacheTokenStore;
import org.marsik.elshelves.backend.entities.Box;
import org.marsik.elshelves.backend.entities.User;
import org.marsik.elshelves.backend.services.IdentifiedEntityService;
import org.marsik.elshelves.backend.services.IdentifiedEntityServiceInterface;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

public class IdsControllerTest extends BaseIntegrationTest {
    @Autowired
    IdentifiedEntityServiceInterface service;

    @Test
    @Ignore
    public void testListIdsRestriction() throws Exception {
        given().
                contentType(ContentType.JSON).
        when().
                get("/v1/id").
        then().
                statusCode(400);
    }

    @Test
    @Ignore
    public void testListIds() throws Exception {
        Box b = new Box();
        b.setId(UUID.randomUUID());
        b.setName("Test box");

        doReturn(b).when(service).get(eq(b.getId()), Mockito.any(User.class));

        given().
                contentType(ContentType.JSON).
        when().
                get("/v1/id?ids[]=" + b.getId().toString()).
        then().
                statusCode(200);
    }
}
