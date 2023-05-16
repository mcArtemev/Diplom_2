package ApiTests.User;

import ApiTests.BaseData;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;

public class LoginUser {
    UserTestFixtures userTestFixtures = new UserTestFixtures();
    String email;
    String password;
    String name;
    Response createdUserData;

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
    @Description("Login user with correct data. Return 200 and check status, email, name")
    public void loginUserWithCorrectDataExpected200(){
        email = "lutic@mail.ru";
        password = "123456";
        name = "Lutic";
        UserSerializer userJsonData = new UserSerializer(email, password, name);
        createdUserData = userTestFixtures.createUser(userJsonData);
        userJsonData = new UserSerializer(email, password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(userJsonData)
                .when()
                .post("/api/auth/login");

        response.then().assertThat().statusCode(200);
        Assert.assertTrue(response.as(UserDeserializer.class).isSuccess());
        Assert.assertEquals(response.as(UserDeserializer.class).getUser().getEmail(), email);
        Assert.assertEquals(response.as(UserDeserializer.class).getUser().getName(), name);
    }

    @Test
    @Description("Login user with wrong email. Return 401")
    public void loginUserWithWrongEmailExpected401(){
        email = "lutic1@mail.ru";
        password = "123456";
        UserSerializer userJsonData = new UserSerializer(email, password, name);
        createdUserData = userTestFixtures.createUser(userJsonData);
        userJsonData = new UserSerializer(email+"1", password);
        Response response = given()
                .header("Content-type", "application/json")
                .body(userJsonData)
                .when()
                .post("/api/auth/login");

        response.then().assertThat().statusCode(401);
        Assert.assertEquals(response.as(UserDeserializer.class).getMessage(), "email or password are incorrect");
        Assert.assertFalse(response.as(UserDeserializer.class).isSuccess());
    }

    @Test
    @Description("Login user with wrong password. Return 401")
    public void loginUserWithWrongPasswordExpected401(){
        email = "lutic1@mail.ru";
        password = "123456";
        UserSerializer userJsonData = new UserSerializer(email, password, name);
        createdUserData = userTestFixtures.createUser(userJsonData);
        userJsonData = new UserSerializer(email, password+"1");
        Response response = given()
                .header("Content-type", "application/json")
                .body(userJsonData)
                .when()
                .post("/api/auth/login");

        response.then().assertThat().statusCode(401);
        Assert.assertEquals(response.as(UserDeserializer.class).getMessage(), "email or password are incorrect");
        Assert.assertFalse(response.as(UserDeserializer.class).isSuccess());
    }
}
