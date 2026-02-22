package com.gully.ggos;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.gully.ggos.api.auth.dto.LoginRequest;
import com.gully.ggos.api.crm.dto.LeadRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

class ContractApiTest extends IntegrationTestBase {
    private static final UUID DEFAULT_ORG_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String PASSWORD_HASH = "$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5Q1CDe7v4xYueAA6QbV0I1f3wWg5G";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seedNonAdminUser() {
        jdbcTemplate.update("delete from user_roles where user_id in (select id from users where email = ?)",
                "user@ggos.local");
        jdbcTemplate.update("delete from users where email = ?", "user@ggos.local");
        jdbcTemplate.update("delete from roles where code = ? and org_id = ?", "SALES", DEFAULT_ORG_ID);

        UUID roleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        jdbcTemplate.update(
                "insert into roles (id, org_id, code, name) values (?, ?, ?, ?)",
                roleId, DEFAULT_ORG_ID, "SALES", "Sales");
        jdbcTemplate.update(
                "insert into users (id, org_id, email, password_hash, full_name, status) values (?, ?, ?, ?, ?, ?)",
                userId, DEFAULT_ORG_ID, "user@ggos.local", PASSWORD_HASH, "Standard User", "ACTIVE");
        jdbcTemplate.update(
                "insert into user_roles (user_id, role_id) values (?, ?)",
                userId, roleId);
    }

    @Test
    void authAndCrmAndKpiContracts() {
        String token = login("admin@ggos.local", "password");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/auth/me")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("permissions", notNullValue());

        LeadRequest lead = new LeadRequest();
        lead.setName("Contract Lead");
        lead.setEmail("contract@lead.com");
        lead.setStatus("OPEN");

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lead)
                .when()
                .post("/api/crm/leads")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("name", equalTo("Contract Lead"));

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/crm/leads")
                .then()
                .statusCode(200)
                .body("content", notNullValue());

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/kpis")
                .then()
                .statusCode(200);
    }

    @Test
    void rbacAdminAccess() {
        String adminToken = login("admin@ggos.local", "password");
        String userToken = login("user@ggos.local", "password");

        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/api/admin/permissions")
                .then()
                .statusCode(403);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/admin/permissions")
                .then()
                .statusCode(200);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/api/admin/roles")
                .then()
                .statusCode(200);
    }

    private String login(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

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
