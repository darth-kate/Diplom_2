package tests;
import org.junit.*;
import serial.UserRegisterSerial;
import steps.UserSteps;
import urls.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.junit4.*;

import static org.apache.http.HttpStatus.*;

public class UserPatchTests {
    private static String email;
    private static String name;
    private static String accessToken;
    private static UserRegisterSerial user;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = Urls.URL;
        UserSteps userSteps = new UserSteps();
        user = UserRegisterSerial.builder()
                .email(userSteps.generateString() + "@yandex.ru")
                .password("test_password")
                .name("Ivan")
                .build();
        Response registerUser = userSteps.registerUser(user.getEmail(), user.getPassword(), user.getName());
        userSteps.assertStatusCode(registerUser, SC_OK);

        Response loginUser = userSteps.loginUser(user.getEmail(), user.getPassword());
        accessToken = userSteps.extractToken(loginUser);
    }

    @Test
    @DisplayName("PATCH /auth/user with valid token")
    public void validPatchUserTest(){
        UserSteps steps = new UserSteps();
        email = steps.generateString() + "@yandex.ru";
        name = name + "1";
        Response response = steps.updateUser(accessToken, email, name);
        steps.assertStatusCode(response, SC_OK);
        steps.compareSuccess(response, true);
        steps.compareEmail(response, email);
        steps.compareName(response, name);
    }

    @Test
    @DisplayName("PATCH /auth/user without token")
    public void WithoutTokenPatchUserTest(){
        UserSteps steps = new UserSteps();
        email = steps.generateString() + "@yandex.ru";
        name = name + "1";
        Response response = steps.updateUser("", email, name);
        steps.assertStatusCode(response, SC_UNAUTHORIZED);
        steps.compareSuccess(response, false);
        steps.compareMessage(response, "You should be authorised");
    }

    @AfterClass
    public static void coolDown(){
        UserSteps steps = new UserSteps();
        Response response = steps.deleteUser(accessToken);
        steps.assertStatusCode(response, SC_ACCEPTED);
    }
}
