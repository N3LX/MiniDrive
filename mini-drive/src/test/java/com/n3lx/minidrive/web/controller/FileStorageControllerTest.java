package com.n3lx.minidrive.web.controller;

import com.n3lx.minidrive.entity.User;
import com.n3lx.minidrive.mapper.UserMapper;
import com.n3lx.minidrive.security.jwt.JWTUtil;
import com.n3lx.minidrive.service.UserService;
import com.n3lx.minidrive.utils.PropertiesUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FileStorageControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    PropertiesUtil propertiesUtil;

    @BeforeEach
    @AfterEach
    void clearRootDirectory() throws IOException {
        var rootDirPath = Paths.get(propertiesUtil.getRootDirAbsolutePath());
        try (var filePaths = Files.walk(rootDirPath)) {
            filePaths
                    .filter(path -> !path.equals(rootDirPath))
                    .forEach(path -> FileSystemUtils.deleteRecursively(new File(path.toUri())));
        }
    }

    @BeforeEach
    void createTestUser() {
        try {
            userService.create(userMapper.mapToDTO(getTestUser()));
        } catch (IllegalArgumentException ignored) {
        }
    }

    @AfterEach
    void deleteTestUser() {
        var userId = userService.getByUsername(getTestUser().getUsername()).getId();
        userService.delete(userId);
    }

    @AfterAll
    void delete() {
        FileSystemUtils.deleteRecursively(new File(propertiesUtil.getRootDirAbsolutePath()));
    }

    User getTestUser() {
        return User.builder()
                .username("testUser")
                .password("12345678")
                .build();
    }

    Path getTestUserDirectoryPath() {
        var userDTO = userService.getByUsername("testUser");
        return Paths.get(propertiesUtil.getRootDirAbsolutePath(), String.valueOf(userDTO.getId())).normalize();
    }

    Path getTestFilePath() {
        return Paths.get("src/test/resources/Bee Movie Transcript.txt").toAbsolutePath().normalize();
    }

    void copyTestFileToTestUserDirectory() {
        try {
            getTestUserDirectoryPath().toFile().mkdir();
            Files.copy(getTestFilePath(), getTestUserDirectoryPath().resolve(getTestFilePath().getFileName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void upload_validPayload_fileWrittenToStorage() throws IOException {
        given()
                .port(port)
                .auth().oauth2(jwtUtil.generateToken(getTestUser()))
                .multiPart(getTestFilePath().toFile())
                .when()
                .post("/api/storage/upload")
                .then()
                .statusCode(201);

        var actualUploadedFilePath = getTestUserDirectoryPath().resolve(getTestFilePath().getFileName());
        assertTrue(actualUploadedFilePath.toFile().exists());
        assertEquals(-1L, Files.mismatch(getTestFilePath(), actualUploadedFilePath));
    }

    @Test
    public void upload_noPayload_returnsBadRequest() {
        given()
                .port(port)
                .auth().oauth2(jwtUtil.generateToken(getTestUser()))
                .when()
                .post("/api/storage/upload")
                .then()
                .statusCode(400);
    }

    @Test
    public void upload_sameFileNameAlreadyInStore_returnsBadRequest() {
        copyTestFileToTestUserDirectory();

        given()
                .port(port)
                .auth().oauth2(jwtUtil.generateToken(getTestUser()))
                .multiPart(getTestFilePath().toFile())
                .when()
                .post("/api/storage/upload")
                .then()
                .statusCode(400)
                .body("message", equalTo("File \"" + getTestFilePath().getFileName().toString()
                        + "\" already exists"))
                .body("timestamp", notNullValue());
    }

    @Test
    public void listFiles_noFileUploaded_returnsEmptyList() {
        given()
                .port(port)
                .auth().oauth2(jwtUtil.generateToken(getTestUser()))
                .when()
                .get("/api/storage/listfiles")
                .then()
                .statusCode(200)
                .body(equalTo("[]"));
    }

    @Test
    public void listFiles_fileInStore_returnsListOfOneFile() {
        copyTestFileToTestUserDirectory();
        given()
                .port(port)
                .auth().oauth2(jwtUtil.generateToken(getTestUser()))
                .when()
                .get("/api/storage/listfiles")
                .then()
                .statusCode(200)
                .body(equalTo("[\"" + getTestFilePath().getFileName() + "\"]"));
    }

    @Test
    public void delete_fileInStore_deletesFireFromStorage() {
        copyTestFileToTestUserDirectory();

        given()
                .port(port)
                .auth().oauth2(jwtUtil.generateToken(getTestUser()))
                .multiPart("fileName", getTestFilePath().getFileName().toString())
                .when()
                .delete("/api/storage/delete")
                .then()
                .statusCode(204);

        assertFalse(getTestUserDirectoryPath().resolve(getTestFilePath().getFileName()).toFile().exists());
    }

    @Test
    public void delete_fileNotInStore_returnsBadRequest() {
        given()
                .port(port)
                .auth().oauth2(jwtUtil.generateToken(getTestUser()))
                .multiPart("fileName", getTestFilePath().getFileName().toString())
                .when()
                .delete("/api/storage/delete")
                .then()
                .statusCode(400)
                .body("message", equalTo("File \"" + getTestFilePath().getFileName().toString()
                        + "\" does not exist"))
                .body("timestamp", notNullValue());
    }

    @Test
    public void rename_fileInStore_renamesFile() {
        var newFileName = "File.txt";
        copyTestFileToTestUserDirectory();

        given()
                .port(port)
                .auth().oauth2(jwtUtil.generateToken(getTestUser()))
                .multiPart("currentFileName", getTestFilePath().getFileName().toString())
                .multiPart("newFileName", newFileName)
                .when()
                .patch("/api/storage/rename")
                .then()
                .statusCode(201);

        assertFalse(getTestUserDirectoryPath().resolve(getTestFilePath().getFileName()).toFile().exists());
        assertTrue(getTestUserDirectoryPath().resolve(newFileName).toFile().exists());
    }

    @Test
    public void rename_fileNotInStore_returnsBadRequest() {
        var newFileName = "File.txt";

        given()
                .port(port)
                .auth().oauth2(jwtUtil.generateToken(getTestUser()))
                .multiPart("currentFileName", getTestFilePath().getFileName().toString())
                .multiPart("newFileName", newFileName)
                .when()
                .patch("/api/storage/rename")
                .then()
                .statusCode(400)
                .body("message", equalTo("File \"" + getTestFilePath().getFileName().toString()
                        + "\" does not exist"))
                .body("timestamp", notNullValue());

        assertFalse(getTestUserDirectoryPath().resolve(newFileName).toFile().exists());
    }

}
