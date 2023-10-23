package com.n3lx.minidrive.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityFilterChainTest {

    @LocalServerPort
    private int port;

    @Test
    public void upload_unauthenticated_forbiddenStatus() {
        given()
                .port(port)
                .when()
                .get("/api/storage/upload")
                .then()
                .statusCode(403)
                .body("error", equalTo("Forbidden"))
                .body("path", equalTo("/api/storage/upload"))
                .body("status", equalTo(403))
                .body("timestamp", notNullValue());
    }

    @Test
    public void listFiles_unauthenticated_forbiddenStatus() {
        given()
                .port(port)
                .when()
                .get("/api/storage/listfiles")
                .then()
                .statusCode(403)
                .body("error", equalTo("Forbidden"))
                .body("path", equalTo("/api/storage/listfiles"))
                .body("status", equalTo(403))
                .body("timestamp", notNullValue());
    }

    @Test
    public void load_unauthenticated_forbiddenStatus() {
        given()
                .port(port)
                .when()
                .get("/api/storage/load")
                .then()
                .statusCode(403)
                .body("error", equalTo("Forbidden"))
                .body("path", equalTo("/api/storage/load"))
                .body("status", equalTo(403))
                .body("timestamp", notNullValue());
    }

    @Test
    public void delete_unauthenticated_forbiddenStatus() {
        given()
                .port(port)
                .when()
                .delete("/api/storage/delete")
                .then()
                .statusCode(403)
                .body("error", equalTo("Forbidden"))
                .body("path", equalTo("/api/storage/delete"))
                .body("status", equalTo(403))
                .body("timestamp", notNullValue());
    }

    @Test
    public void rename_unauthenticated_forbiddenStatus() {
        given()
                .port(port)
                .when()
                .patch("/api/storage/rename")
                .then()
                .statusCode(403)
                .body("error", equalTo("Forbidden"))
                .body("path", equalTo("/api/storage/rename"))
                .body("status", equalTo(403))
                .body("timestamp", notNullValue());
    }

    @Test
    public void whoami_unauthenticated_notForbiddenStatus() {
        given()
                .port(port)
                .when()
                .get("/api/auth/whoami")
                .then()
                .statusCode(not(403));
    }

    @Test
    public void register_unauthenticated_notForbiddenStatus() {
        given()
                .port(port)
                .when()
                .get("/api/auth/register")
                .then()
                .statusCode(not(403));
    }

    @Test
    public void login_unauthenticated_notForbiddenStatus() {
        given()
                .port(port)
                .when()
                .get("/api/auth/login")
                .then()
                .statusCode(not(403));
    }

}
