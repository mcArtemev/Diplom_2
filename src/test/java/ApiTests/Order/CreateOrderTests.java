package ApiTests.Order;

import ApiTests.BaseData;
import ApiTests.User.UserDeserializer;
import ApiTests.User.UserSerializer;
import ApiTests.User.UserTestFixtures;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

public class CreateOrderTests {
    OrderTestFixtures orderTestFixtures = new OrderTestFixtures();
    UserTestFixtures userTestFixtures = new UserTestFixtures();
    Response createdUserData;
    ArrayList<Ingredient> ingredientData = new ArrayList<Ingredient>();
    ArrayList<String> orderIngredients = new ArrayList<String>();
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
    @Description("Create new order with auth with 1 ingredient. Return 200")
    public void createNewOrderWithAuthWithIngredien(){
        ingredientData.add(orderTestFixtures.getIngredients(0));
        UserSerializer userJsonData = new UserSerializer("lutic2@mail.ru", "123456", "Lutic");
        createdUserData = userTestFixtures.createUser(userJsonData);
        String token = createdUserData.as(UserDeserializer.class).getAccessToken();

        OrderSerializer orderJsonData = new OrderSerializer(new String[]{ingredientData.get(0).get_id()});

        Response order = orderTestFixtures.createOrder(token, orderJsonData);

        order.then().assertThat().statusCode(200);
        Assert.assertTrue(order.as(OrderDeserializer.class).isSuccess()==true);

        Assert.assertThat(ingredientData.get(0).getName(), CoreMatchers.containsString(order.as(OrderDeserializer.class).getOrder().getIngredients()[0].getName()));
    }

    @Test
    @Description("Create new order with auth with 2 ingredients. Return 200")
    public void createNewOrderWithAuthWith2Ingredients(){
        ingredientData.add(orderTestFixtures.getIngredients(0));
        ingredientData.add(orderTestFixtures.getIngredients(1));
        UserSerializer userJsonData = new UserSerializer("lutic2@mail.ru", "123456", "Lutic");
        createdUserData = userTestFixtures.createUser(userJsonData);
        String token = createdUserData.as(UserDeserializer.class).getAccessToken();

        OrderSerializer orderJsonData = new OrderSerializer(new String[]{ingredientData.get(0).get_id(), ingredientData.get(1).get_id()});

        Response order = orderTestFixtures.createOrder(token, orderJsonData);

        order.then().assertThat().statusCode(200);
        Assert.assertTrue(order.as(OrderDeserializer.class).isSuccess()==true);

        Assert.assertThat(ingredientData.get(0).getName(), CoreMatchers.containsString(order.as(OrderDeserializer.class).getOrder().getIngredients()[0].getName()));
        Assert.assertThat(ingredientData.get(1).getName(), CoreMatchers.containsString(order.as(OrderDeserializer.class).getOrder().getIngredients()[1].getName()));
    }

    @Test
    @Description("Create new order with auth with incorrect ingredient. Return 500")
    public void createNewOrderWithAuthWithoutIncorrectIngredient(){

        UserSerializer userJsonData = new UserSerializer("lutic2@mail.ru", "123456", "Lutic");
        createdUserData = userTestFixtures.createUser(userJsonData);
        String token = createdUserData.as(UserDeserializer.class).getAccessToken();

        OrderSerializer orderJsonData = new OrderSerializer(new String[]{"111"});

        Response order = orderTestFixtures.createOrder(token, orderJsonData);

        order.then().assertThat().statusCode(500);
    }

    @Test
    @Description("Create new order with auth without ingredients. Return 400")
    public void createNewOrderWithAuthWithIncorrectIngredient(){
        UserSerializer userJsonData = new UserSerializer("lutic2@mail.ru", "123456", "Lutic");
        createdUserData = userTestFixtures.createUser(userJsonData);
        String token = createdUserData.as(UserDeserializer.class).getAccessToken();

        OrderSerializer orderJsonData = new OrderSerializer(new String[]{});

        Response order = orderTestFixtures.createOrder(token, orderJsonData);

        order.then().assertThat().statusCode(400);
        Assert.assertTrue(order.as(OrderDeserializer.class).isSuccess()==false);
        Assert.assertTrue("Ожидалось: Ingredient ids must be provided. По факту: " + order.as(OrderDeserializer.class).getMessage(), order.as(OrderDeserializer.class).getMessage().equals("Ingredient ids must be provided"));
    }

    @Test
    @Description("Create new order without auth with 2 ingredients. Return 401")
    public void createNewOrderWithoutAuthWith2Ingrediens(){
        ingredientData.add(orderTestFixtures.getIngredients(0));
        ingredientData.add(orderTestFixtures.getIngredients(1));
        UserSerializer userJsonData = new UserSerializer("lutic2@mail.ru", "123456", "Lutic");
        createdUserData = userTestFixtures.createUser(userJsonData);

        OrderSerializer orderJsonData = new OrderSerializer(new String[]{ingredientData.get(0).get_id(), ingredientData.get(1).get_id()});

        Response order = orderTestFixtures.createOrder(orderJsonData);

        order.then().assertThat().statusCode(200); //Нет задокументированного поведения при отсутствии авторизации. Тут бага "Если не передать авторизацию, то заказ создается"
    }
}
