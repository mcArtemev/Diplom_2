package ApiTests.Order;

import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class OrderTestFixtures {
    public Response createOrder(String userToken, OrderSerializer orderJsonData) {
        Response response = given()
                .headers("Authorization", userToken, "Content-Type", "application/json")
                .and()
                .body(orderJsonData)
                .when()
                .post("/api/orders");
        return response;
    }
    public Response createOrder(OrderSerializer orderJsonData) {
        Response response = given()
                .header("Content-Type", "application/json")
                .and()
                .body(orderJsonData)
                .when()
                .post("/api/orders");
        return response;
    }

    public Ingredient getIngredients(int number) {
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/ingredients");

        Ingredient[] ingredient = response.as(Ingredients.class).getData();

        return ingredient[number];
    }
}
