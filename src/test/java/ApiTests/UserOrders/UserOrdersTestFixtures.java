package ApiTests.UserOrders;

import ApiTests.BaseData;
import ApiTests.Order.Ingredient;
import ApiTests.Order.OrderSerializer;
import ApiTests.Order.OrderTestFixtures;
import io.restassured.response.Response;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class UserOrdersTestFixtures extends BaseData {
    OrderTestFixtures orderTestFixtures = new OrderTestFixtures();
    ArrayList<Ingredient> ingredientData = new ArrayList<Ingredient>();
    public Response userOrders(String userToken) {
        Response response = given()
                .header("Authorization", userToken)
                .when()
                .get("/api/orders");
        return response;
    }

    public Response addUserOrder(String userToken) {
        ingredientData.add(orderTestFixtures.getIngredients(0));
        ingredientData.add(orderTestFixtures.getIngredients(1));
        OrderSerializer orderJsonData = new OrderSerializer(new String[]{ingredientData.get(0).get_id(), ingredientData.get(1).get_id()});
        return orderTestFixtures.createOrder(userToken, orderJsonData);
    }
}
