package ApiTests.User;

import ApiTests.BaseData;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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
        MatcherAssert.assertThat(response.as(UserDeserializer.class).isSuccess(), equalTo(true));
        MatcherAssert.assertThat(response.as(UserDeserializer.class).getUser().getEmail(), equalTo(email));
        MatcherAssert.assertThat(response.as(UserDeserializer.class).getUser().getName(), equalTo(name));
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
        MatcherAssert.assertThat(response.as(UserDeserializer.class).getMessage(), equalTo("email or password are incorrect"));
        MatcherAssert.assertThat(response.as(UserDeserializer.class).isSuccess(), equalTo(false));
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
        MatcherAssert.assertThat(response.as(UserDeserializer.class).getMessage(), equalTo("email or password are incorrect"));
        MatcherAssert.assertThat(response.as(UserDeserializer.class).isSuccess(), equalTo(false));
    }
}
