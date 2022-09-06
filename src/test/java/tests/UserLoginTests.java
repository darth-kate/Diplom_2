package tests;
import org.junit.*;
import serial.UserRegisterSerial;
import steps.UserSteps;
import urls.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.junit4.*;
import static org.apache.http.HttpStatus.*;

public class UserLoginTests {
    private static String accessToken;

    private static UserRegisterSerial user;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = Urls.URL;
        UserSteps steps = new UserSteps();
        user = UserRegisterSerial.builder()
                .email(steps.generateString() + "@yandex.ru")
                .password("test_password")
                .name("Ivan")
                .build();

        Response registerUser = steps.registerUser(user.getEmail(), user.getPassword(), user.getName());
        steps.assertStatusCode(registerUser, SC_OK);
        Response loginUser = steps.loginUser(user.getEmail(), user.getPassword());
        accessToken = steps.extractToken(loginUser);
    }

    @Test
    @DisplayName("POST /auth/login with valid credentials")
    public void validLoginTest(){
        UserSteps steps = new UserSteps();
        Response response = steps.loginUser(user.getEmail(), user.getPassword());
        steps.compareSuccess(response, true);
        steps.assertStatusCode(response, SC_OK);
    }

    @Test
    @DisplayName("POST /auth/login with invalid email")
    public void wrongEmailTest(){
        UserSteps steps = new UserSteps();
        Response response = steps.loginUser(steps.generateString() + "@yandex.ru", user.getPassword());
        steps.compareSuccess(response, false);
        steps.compareMessage(response, "email or password are incorrect");
        steps.assertStatusCode(response, SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST /auth/login with invalid password")
    public void wrongPasswordTest(){
        UserSteps steps = new UserSteps();
        Response response = steps.loginUser(user.getEmail(), steps.generateString());
        steps.compareSuccess(response, false);
        steps.compareMessage(response, "email or password are incorrect");
        steps.assertStatusCode(response, SC_UNAUTHORIZED);
    }

    @AfterClass
    public static void coolDown(){
        UserSteps steps = new UserSteps();
        Response response = steps.deleteUser(accessToken);
        steps.assertStatusCode(response, SC_ACCEPTED);
    }
}
