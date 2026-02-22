package com.gully.ggos;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.gully.ggos.api.auth.dto.LoginRequest;
import com.gully.ggos.api.crm.dto.LeadRequest;
import com.gully.ggos.api.crm.dto.LeadResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class CrmLeadApiTest extends IntegrationTestBase {

    @Test
    void createLeadAndList() {
        String token = login();

        LeadRequest request = new LeadRequest();
        request.setName("Acme Lead");
        request.setEmail("lead@acme.com");
        request.setStatus("OPEN");

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/crm/leads")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Acme Lead"));

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/crm/leads")
                .then()
                .statusCode(200)
                .body("content.size()", notNullValue());
    }

    private String login() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@ggos.local");
        request.setPassword("password");

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
