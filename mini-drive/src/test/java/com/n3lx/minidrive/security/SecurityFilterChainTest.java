package com.n3lx.minidrive.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityFilterChainTest {

    @LocalServerPort
    private int port;

    @Test
    public void whoAmI_unauthenticated_unauthorizedStatus() {
        given()
                .port(port)
                .when()
                .get("/api/auth/whoami")
                .then()
                .statusCode(401)
                .body("message", equalTo("Unauthenticated user"))
                .body("timestamp", notNullValue());
    }

}
