package com.n3lx.minidrive.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OpenApiTest {

    @LocalServerPort
    private int port;

    @Test
    public void connectToOpenApiPage_unauthenticated_returnsOkStatus() {
        given()
                .port(port)
                .when()
                .get("/swagger-ui/index.html")
                .then()
                .statusCode(200);
    }

}
