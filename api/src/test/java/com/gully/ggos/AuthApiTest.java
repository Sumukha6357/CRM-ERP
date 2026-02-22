package com.gully.ggos;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import com.gully.ggos.api.auth.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class AuthApiTest extends IntegrationTestBase {

    @Test
    void loginAndMe() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@ggos.local");
        request.setPassword("password");

        String token = given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .extract()
            .path("token");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/auth/me")
        .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("permissions", notNullValue());
    }
}
