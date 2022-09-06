package steps;
import io.restassured.path.json.JsonPath;
import serial.UserLoginSerial;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import serial.UserPatchSerial;
import serial.UserRegisterSerial;
import urls.Urls;

import java.util.UUID;

import static org.junit.Assert.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserSteps {
    private static String accessToken;

    @Step("Generate random string for unique credentials")
    public String generateString(){
        String generatedString = UUID.randomUUID().toString();
        return generatedString;
        }

    @Step("Register user")
    public Response registerUser(String email, String password, String name) {
        UserRegisterSerial user = new UserRegisterSerial(email, password, name);
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(Urls.USER_REGISTER);
    }

    @Step("Extract accessToken")
    public String extractToken(Response response) {
        JsonPath jsonPathEvaluator = response.jsonPath();
        String str = jsonPathEvaluator.get("accessToken");
        return str.split("Bearer ")[1];
    }

    @Step("Login user")
    public Response loginUser(String email, String password) {
        UserLoginSerial user = new UserLoginSerial(email, password);
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(Urls.USER_LOGIN);
    }

    @Step("Delete user")
    public Response deleteUser(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .header("Content-type", "application/json")
                .when()
                .delete(Urls.USER_DELETE_AND_PATCH);
    }

    @Step("Assert status code of response")
    public void assertStatusCode(Response response, int statusCode) {
        response.then().assertThat().statusCode(statusCode);
    }

    @Step("Compare success field")
    public void compareSuccess(Response response, boolean success) {
        response.then().assertThat().body("success", equalTo(success));
    }

    @Step("Compare message field")
    public void compareMessage(Response response, String message) {
        response.then().assertThat().body("message", equalTo(message));
    }

    @Step("Update user info")
    public Response updateUser(String accessToken, String email, String name) {
        UserPatchSerial user = new UserPatchSerial(email, name);
        return given()
                .auth().oauth2(accessToken)
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(Urls.USER_DELETE_AND_PATCH);
    }

    @Step("Compare user.email")
    public void compareEmail(Response response, String email) {
        JsonPath jsonPathEvaluator = response.jsonPath();
        String new_email = jsonPathEvaluator.get("user.email");
        assertEquals(email, new_email);
    }

    @Step("Compare user.name")
    public void compareName(Response response, String name) {
        JsonPath jsonPathEvaluator = response.jsonPath();
        String new_name = jsonPathEvaluator.get("user.name");
        assertEquals(name, new_name);
    }
}
