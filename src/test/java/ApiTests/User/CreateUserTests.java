package ApiTests.User;

import ApiTests.BaseData;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTests {
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
    @Description("Create new user with unique data. Return 200 and check email, name, password")
    public void createUniqueUserExpected200(){
        email = "lutic2@mail.ru";
        password = "123456";
        name = "Lutic";
        UserSerializer userJsonData = new UserSerializer(email, password, name);
        createdUserData = userTestFixtures.createUser(userJsonData);

        createdUserData.then().assertThat().statusCode(200);
        MatcherAssert.assertThat(createdUserData.as(UserDeserializer.class).getUser().getEmail(), equalTo(email));
        MatcherAssert.assertThat(createdUserData.as(UserDeserializer.class).getUser().getName(), equalTo(name));
        MatcherAssert.assertThat(createdUserData.as(UserDeserializer.class).isSuccess(), equalTo(true));
    }

    @Test
    @Description("Create new user with exist data. Return 403")
    public void createExistUserExpected403(){
        email = "lutic2@mail.ru";
        password = "123456";
        name = "Lutic";
        UserSerializer userJsonData = new UserSerializer(email, password, name);
        createdUserData = userTestFixtures.createUser(userJsonData);
        Response existUserData = userTestFixtures.createUser(userJsonData);

        existUserData.then().assertThat().statusCode(403);
        MatcherAssert.assertThat(existUserData.as(UserDeserializer.class).isSuccess(), equalTo(false));
        MatcherAssert.assertThat(existUserData.as(UserDeserializer.class).getMessage(), equalTo("User already exists"));
    }

    @Test
    @Description("Create new user without email. Return 403")
    public void createUserWithoutEmailExpected403(){
        email = null;
        password = "123456";
        name = "Lutic";
        UserSerializer userJsonData = new UserSerializer(email, password, name);
        createdUserData = userTestFixtures.createUser(userJsonData);
        Response existUserData = userTestFixtures.createUser(userJsonData);

        existUserData.then().assertThat().statusCode(403);
        MatcherAssert.assertThat(existUserData.as(UserDeserializer.class).isSuccess(), equalTo(false));
        MatcherAssert.assertThat(existUserData.as(UserDeserializer.class).getMessage(), equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Create new user without password. Return 403")
    public void createUserWithoutPasswordExpected403(){
        email = "lutic2@mail.ru";
        password = null;
        name = "Lutic";
        UserSerializer userJsonData = new UserSerializer(email, password, name);
        createdUserData = userTestFixtures.createUser(userJsonData);
        Response existUserData = userTestFixtures.createUser(userJsonData);

        existUserData.then().assertThat().statusCode(403);
        MatcherAssert.assertThat(existUserData.as(UserDeserializer.class).isSuccess(), equalTo(false));
        MatcherAssert.assertThat(existUserData.as(UserDeserializer.class).getMessage(), equalTo("Email, password and name are required fields"));
    }

    @Test
    @Description("Create new user without name. Return 403")
    public void createUserWithoutNameExpected403(){
        email = "lutic2@mail.ru";
        password = "123456";
        name = null;
        UserSerializer userJsonData = new UserSerializer(email, password, name);
        createdUserData = userTestFixtures.createUser(userJsonData);
        Response existUserData = userTestFixtures.createUser(userJsonData);

        existUserData.then().assertThat().statusCode(403);
        MatcherAssert.assertThat(existUserData.as(UserDeserializer.class).isSuccess(), equalTo(false));
        MatcherAssert.assertThat(existUserData.as(UserDeserializer.class).getMessage(), equalTo("Email, password and name are required fields"));
    }
}
