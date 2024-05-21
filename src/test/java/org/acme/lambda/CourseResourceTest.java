package org.acme.lambda;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.lambda.model.Course;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class CourseResourceTest {
    @Test
    void getLambdaResponseSuccess() throws Exception {
        // you test your lambdas by invoking on http://localhost:8081
        // this works in dev mode too

        given()
                .baseUri("http://localhost:8081/api")
                .contentType("application/json")
                .accept("application/json")
//                .body()
                .when()
                .get("/courses")
                .then()
                .statusCode(200);
//                .body(containsString("Course 1"))
//                .body("size()", is(3))
//                .body("title", hasItem("Course 1"))
//                .body("author", hasItem("AD"));
    }

    @Test
    void postLambdaResponseSuccess() throws Exception {
        // you test your lambdas by invoking on http://localhost:8081
        // this works in dev mode too

        Course in = new Course();
        in.setDescription("My Course");

        given()
                .baseUri("http://localhost:8081/api")
                .contentType("application/json")
                .accept("application/json")
                .body(in)
                .when()
                .post("/courses")
                .then()
                .statusCode(200)
//                .body(containsString("Course 1"))
//                .body("size()", is(3))
                .body("description", is("My Course"))
                .body("id", notNullValue());
    }
}
