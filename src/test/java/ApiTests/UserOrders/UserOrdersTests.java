package ApiTests.UserOrders;

import ApiTests.BaseData;
import ApiTests.User.UserDeserializer;
import ApiTests.User.UserSerializer;
import ApiTests.User.UserTestFixtures;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserOrdersTests {
    Response createdUserData;
    UserTestFixtures userTestFixtures = new UserTestFixtures();
    UserOrdersTestFixtures userOrdersTestFixtures = new UserOrdersTestFixtures();
    @Before
    public void setUp() {
        RestAssured.baseURI = BaseData.baseURI;
    }

    @After
    public void tearDown() {
        if(createdUserData.statusCode()==200) {
            userTestFixtures.deleteUser(createdUserData.as(UserDeserializer.class).getAccessToken());
        }
    }

    @Test
    @Description("Get user orders with auth. Return 200")
    public void getUserOrderWithAuth(){
        UserSerializer userJsonData = new UserSerializer("lutic@mail.ru", "123456", "Lutic");
        createdUserData = userTestFixtures.createUser(userJsonData);
        String token = createdUserData.as(UserDeserializer.class).getAccessToken();
        userOrdersTestFixtures.addUserOrder(token);

        Response userOrders = userOrdersTestFixtures.userOrders(token);
        userOrders.then().assertThat().statusCode(200);
        Assert.assertFalse("Заказы пустые", userOrders.as(UserOrdersDeserializer.class).getOrders().isEmpty());
    }

    @Test
    @Description("Get user orders without auth. Return 401")
    public void getUserOrderWithoutAuth(){
        UserSerializer userJsonData = new UserSerializer("lutic@mail.ru", "123456", "Lutic");
        createdUserData = userTestFixtures.createUser(userJsonData);

        Response userOrders = userOrdersTestFixtures.userOrders("");
        userOrders.then().assertThat().statusCode(401);
        Assert.assertTrue("Статус некорректен. По факту: " + userOrders.as(UserOrdersDeserializer.class).getMessage(), userOrders.as(UserOrdersDeserializer.class).getMessage().equals("You should be authorised"));
    }
}
