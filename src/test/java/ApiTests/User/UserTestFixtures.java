package ApiTests.User;

import ApiTests.BaseData;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class UserTestFixtures extends BaseData {
    public Response createUser(UserSerializer userJsonData) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(userJsonData)
                .when()
                .post("/api/auth/register");
        return response;
    }

    public void deleteUser(String userToken) {
         given()
                .header("Authorization", userToken)
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(202);
    }

    public Response updateUserData(String userToken, UserSerializer userJsonData) {
        Response response = given()
                .header("Authorization", userToken)
                .header("Content-Type", "application/json")
                .and()
                .body(userJsonData)
                .when()
                .patch("/api/auth/user");
        return response;
    }

    public Response updateUserData(UserSerializer userJsonData) {
        Response response = given()
                .header("Content-Type", "application/json")
                .and()
                .body(userJsonData)
                .when()
                .patch("/api/auth/user");
        return response;
    }

    public Response loginUser(UserSerializer userJsonData) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(userJsonData)
                .when()
                .post("/api/auth/login ");
        return response;
    }


}
