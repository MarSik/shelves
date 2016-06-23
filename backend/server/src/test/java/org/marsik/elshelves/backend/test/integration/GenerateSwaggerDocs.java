package org.marsik.elshelves.backend.test.integration;

import static io.restassured.RestAssured.given;

import org.junit.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.github.robwin.markup.builder.MarkupLanguage;
import io.github.robwin.swagger2markup.Swagger2MarkupConverter;

public class GenerateSwaggerDocs extends BaseIntegrationTest {
    @Test
    public void generateMarkdown() throws Exception {
        Response response = given().
                contentType(ContentType.JSON).
                when().
                get("/v2/api-docs").
                then().
                statusCode(200).
                extract().
                response();

        String swaggerJson = response.body().asString();
        Swagger2MarkupConverter
                .fromString(swaggerJson)
                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                .build()
                .intoFolder("src/docs/markdown/generated");

        Swagger2MarkupConverter
                .fromString(swaggerJson)
                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                .build()
                .intoFolder("src/docs/asciidoc/generated");
    }
}
