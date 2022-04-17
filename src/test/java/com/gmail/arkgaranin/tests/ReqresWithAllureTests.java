package com.gmail.arkgaranin.tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gmail.arkgaranin.helpers.CustomAllureListener.withCustomTemplates;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ReqresWithAllureTests extends TestBase {

  @Test
  void successRegistrTest() {
    Map<String, String> userData = new HashMap<>();
    userData.put("email", "eve.holt@reqres.in");
    userData.put("password", "pistol");

    given()
        .filter(withCustomTemplates())
        .contentType(JSON)
        .log().uri()
        .log().body()
        .body(userData)
        .when()
        .post("/api/register")
        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .body("id", is(4), "token", is("QpwL5tke4Pnpja7X4"));
  }

  @Test
  void unsuccessRegistrTest() {
    Map<String, String> userData = new HashMap<>();
    userData.put("email", "sydney@fife");

    Map<String, String> response = new HashMap<>();
    response.put("error", "Missing password");

    given()
        .filter(withCustomTemplates())
        .contentType(JSON)
        .log().uri()
        .log().body()
        .body(userData)
        .when()
        .post("/api/register")
        .then()
        .log().status()
        .log().body()
        .statusCode(400)
        .body("error", is(response.get("error")));
  }

  @Test
  void successLoginTest() {
    Map<String, String> userData = new HashMap<>();
    userData.put("email", "eve.holt@reqres.in");
    userData.put("password", "cityslicka");

    given()
        .filter(withCustomTemplates())
        .contentType(JSON)
        .log().uri()
        .log().body()
        .body(userData)
        .when()
        .post("/api/login")
        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .body("token", is(notNullValue()));
  }

  @Test
  void unsuccessLoginTest() {
    Map<String, String> userData = new HashMap<>();
    userData.put("email", "peter@klaven");

    given()
        .filter(withCustomTemplates())
        .contentType(JSON)
        .log().uri()
        .log().body()
        .body(userData)
        .when()
        .post("/api/login")
        .then()
        .log().status()
        .log().body()
        .statusCode(400)
        .body("error", is("Missing password"));
  }

  @Test
  void delayedResponseTest() {
    Response response = (Response) given()
        .filter(withCustomTemplates())
        .contentType(JSON)
        .log().uri()
        .log().body()
        .when()
        .get("/api/users?delay=3")
        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .body(matchesJsonSchemaInClasspath("schemas/delayed_response_schema.json"))
        .body("per_page", is(6), "total", is(12))
        .extract().response();

    JsonPath jsonPath = response.jsonPath();
    List<Integer> id = jsonPath.get("data.id");

    assertThat(id.size()).isEqualTo(6);
  }

  @Test
  void listUsersTest() {
    Response response = (Response) given()
        .filter(withCustomTemplates())
        .contentType(JSON)
        .log().uri()
        .log().body()
        .when()
        .get("/api/users?page=2")
        .then()
        .log().status()
        .log().body()
        .statusCode(200)
        .body(matchesJsonSchemaInClasspath("schemas/users_list_schema.json"))
        .body("per_page", is(6), "total", is(12))
        .extract().response();

    JsonPath jsonPath = response.jsonPath();
    List<String> emails = jsonPath.get("data.email");

    assertThat(emails.get(5)).isEqualTo("rachel.howell@reqres.in");
  }

  @Test
  void userNotFoundTest() {
    given()
        .filter(withCustomTemplates())
        .contentType(JSON)
        .log().uri()
        .log().body()
        .when()
        .get("/api/users/23")
        .then()
        .log().status()
        .log().body()
        .statusCode(404);
  }

  @Test
  void createUserTest() {
    Map<String, String> userData = new HashMap<>();
    userData.put("name", "Boris");
    userData.put("job", "QA");

    given()
        .filter(withCustomTemplates())
        .contentType(JSON)
        .body(userData)
        .log().uri()
        .log().body()
        .when()
        .post("/api/users")
        .then()
        .log().status()
        .log().body()
        .statusCode(201)
        .body("name", is("Boris"), "job", is("QA"), "id", is(notNullValue()),
            "createdAt", is(notNullValue()));
  }
}
