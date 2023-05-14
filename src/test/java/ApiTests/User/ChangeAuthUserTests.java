package ApiTests.User;

import ApiTests.BaseData;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ChangeAuthUserTests {
    UserTestFixtures userTestFixtures = new UserTestFixtures();
    Response createdUserData;
    private String email;
    private String password;
    private String name;
    private String newEmail;
    private String newName;
    private String newPassword;
    private boolean isAuthorized;

    public ChangeAuthUserTests(String email, String password, String name, String newEmail, String newName, String newPassword, boolean isAuthorized) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.newEmail = newEmail;
        this.newName = newName;
        this.newPassword = newPassword;
        this.isAuthorized = isAuthorized;
    }

    @Parameterized.Parameters(name = "Test for user: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"user1@example.com", "password1", "User 1", "newuser1@example.com", null, null, true},
                {"user2@example.com", "password2", "User 2", null, "New User 2", null, true},
                {"user3@example.com", "password3", "User 3", null, null, "newpassword3", true},
                {"user4@example.com", "password4", "User 4", "newuser4@example.com", null, null, false},
                {"user5@example.com", "password5", "User 5", null, "New User 5", null, false},
                {"user6@example.com", "password6", "User 6", null, null, "newpassword6", false},
        });
    }

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
    @Description("Update user email. Return 200 and check email")
    public void updateUserData(){
        UserSerializer user = new UserSerializer(email, password, name);

        createdUserData = userTestFixtures.createUser(user);
        createdUserData.then().assertThat().statusCode(200);

        String token = createdUserData.as(UserDeserializer.class).getAccessToken();

        UserSerializer updatedUser = new UserSerializer(newEmail, newPassword, newName);

        if (isAuthorized) {
            Response response = userTestFixtures.updateUserData(token, updatedUser);
            response.then().assertThat().statusCode(200);
            UserDeserializer updatedUserDeserializer = response.as(UserDeserializer.class);
            if (newEmail != null) {
                assertEquals(newEmail, updatedUserDeserializer.getUser().getEmail());
            }
            if (newName != null) {
                assertEquals(newName, updatedUserDeserializer.getUser().getName());
            }
            if (newPassword != null) {
                UserSerializer loginUser = new UserSerializer(email, newPassword, name);
                Response loginResponse = userTestFixtures.loginUser(loginUser);
                loginResponse.then().assertThat().statusCode(200);
            }
        } else if(!isAuthorized){
            Response response = userTestFixtures.updateUserData(updatedUser);
            response.then().assertThat().statusCode(401);
        }
    }
}
