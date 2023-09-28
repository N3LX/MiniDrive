package com.n3lx.minidrive.controller;

import com.n3lx.minidrive.dto.UserDTO;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void clearTables() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    private void registerTestUser() {
        given()
                .port(port)
                .contentType("application/json")
                .body(UserDTO.builder()
                        .username("testUser")
                        .password("12345678")
                        .build())
                .when()
                .post("/api/auth/register")
                .then();
    }

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

    @Test
    public void login_noUsernameInDB_badRequestStatus() {
        given()
                .port(port)
                .contentType("application/json")
                .body(UserDTO.builder()
                        .username("123")
                        .password("123")
                        .build())
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(400)
                .defaultParser(Parser.JSON)
                .body("message", equalTo("Bad credentials"))
                .body("timestamp", notNullValue());
    }

    @Test
    public void login_incorrectCredentialsForExistingUser_badRequestStatus() {
        registerTestUser();

        given()
                .port(port)
                .contentType("application/json")
                .body(UserDTO.builder()
                        .username("testUser")
                        .password("123456789")
                        .build())
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(400)
                .defaultParser(Parser.JSON)
                .body("message", equalTo("Bad credentials"))
                .body("timestamp", notNullValue());
    }

    @Test
    public void login_correctCredentials_successfullyLoggedIn() {
        registerTestUser();

        given()
                .port(port)
                .contentType("application/json")
                .body(UserDTO.builder()
                        .username("testUser")
                        .password("12345678")
                        .build())
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .defaultParser(Parser.JSON)
                .body("username", equalTo("testUser"))
                .body("token", notNullValue());
    }

    @Test
    public void register_correctCredentials_successfullyRegistered() {
        given()
                .port(port)
                .contentType("application/json")
                .body(UserDTO.builder()
                        .username("testUser")
                        .password("12345678")
                        .build())
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(204);
    }

    @Test
    public void register_incorrectPassword_badRequest() {
        given()
                .port(port)
                .contentType("application/json")
                .body(UserDTO.builder()
                        .username("testUser")
                        .password("1234567")
                        .build())
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(400)
                .defaultParser(Parser.JSON)
                .body("message", equalTo("Password must be between 8 and 32 characters in length"))
                .body("timestamp", notNullValue());
    }

    @Test
    public void register_alreadyRegisteredUsername_badRequest() {
        registerTestUser();

        given()
                .port(port)
                .contentType("application/json")
                .body(UserDTO.builder()
                        .username("testUser")
                        .password("12345678")
                        .build())
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(400)
                .defaultParser(Parser.JSON)
                .body("message", equalTo("User with same username already exists"))
                .body("timestamp", notNullValue());
    }

}
