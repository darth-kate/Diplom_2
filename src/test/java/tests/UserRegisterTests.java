package tests;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import serial.UserRegisterSerial;
import steps.UserSteps;
import urls.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.*;

import static org.apache.http.HttpStatus.*;

public class UserRegisterTests {
    private static String email;
    private static String password;
    private static String name;
    private static UserRegisterSerial user;

    @Before
    public void setUp() {
        RestAssured.baseURI = Urls.URL;
        UserSteps steps = new UserSteps();
        user = UserRegisterSerial.builder()
                .email(steps.generateString() + "@yandex.ru")
                .password("test_password")
                .name("Ivan")
                .build();
    }

    @Test
    @DisplayName("POST /auth/register with all fields successfully")
    public void validRegistrationTest() {
        UserSteps steps = new UserSteps();
        Response response = steps.registerUser(user.getEmail(), user.getPassword(), user.getName());
        steps.compareSuccess(response, true);
        steps.assertStatusCode(response, SC_OK);
    }

    @Test
    @DisplayName("POST /auth/register without the name")
    public void registrationWithoutNameTest() {
        UserSteps steps = new UserSteps();
        name = "";
        Response response = steps.registerUser(user.getEmail(), user.getPassword(), name);
        steps.compareSuccess(response, false);
        steps.compareMessage(response, "Email, password and name are required fields");
        steps.assertStatusCode(response, SC_FORBIDDEN);
    }

    @Test
    @DisplayName("POST /auth/register without the email")
    public void registrationWithoutEmailTest() {
        UserSteps steps = new UserSteps();
        email = "";
        Response response = steps.registerUser(email, user.getPassword(), user.getName());
        steps.compareSuccess(response, false);
        steps.compareMessage(response, "Email, password and name are required fields");
        steps.assertStatusCode(response, SC_FORBIDDEN);
    }

    @Test
    @DisplayName("POST /auth/register without the password")
    public void registrationWithoutPasswordTest() {
        UserSteps steps = new UserSteps();
        password = "";
        Response response = steps.registerUser(user.getEmail(), password, user.getName());
        steps.compareSuccess(response, false);
        steps.compareMessage(response, "Email, password and name are required fields");
        steps.assertStatusCode(response, SC_FORBIDDEN);
    }

    @Test
    @DisplayName("POST /auth/register with the same credentials")
    public void sameCredentialsTest() {
        UserSteps steps = new UserSteps();
        Response response1 = steps.registerUser(user.getEmail(), user.getPassword(), user.getName());
        steps.compareSuccess(response1, true);
        steps.assertStatusCode(response1, SC_OK);
        Response response2 = steps.registerUser(user.getEmail(), user.getPassword(), user.getName());
        steps.compareSuccess(response2, false);
        steps.compareMessage(response2, "User already exists");
        steps.assertStatusCode(response2, SC_FORBIDDEN);
    }

    @After
    public void coolDown() {
        if (StringUtils.equalsAny("", email, password, name)) {
            return;
        }

        UserSteps steps = new UserSteps();
        Response response = steps.loginUser(user.getEmail(), user.getPassword());
        String accessToken = steps.extractToken(response);
        steps.deleteUser(accessToken);
    }
}

