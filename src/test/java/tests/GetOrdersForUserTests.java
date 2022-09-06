package tests;
import org.junit.*;
import serial.UserRegisterSerial;
import steps.OrderSteps;
import steps.UserSteps;
import urls.Urls;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.junit4.*;

import static org.apache.http.HttpStatus.*;

public class GetOrdersForUserTests {
    private static String accessToken;
    private static String orderId;
    private static UserRegisterSerial user;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = Urls.URL;
        OrderSteps orderSteps = new OrderSteps();
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

        Response orderResponse  = orderSteps.getIngredients();
        String ingredientOne = orderSteps.extractIngredient(orderResponse, 0);
        String ingredientTwo = orderSteps.extractIngredient(orderResponse, 1);
        String ingredientThree = orderSteps.extractIngredient(orderResponse, 2);

        orderResponse = orderSteps.postOrder(accessToken, new String[]{ingredientOne, ingredientTwo, ingredientThree});
        userSteps.assertStatusCode(orderResponse, SC_OK);
        orderId = orderSteps.extractOrderId(orderResponse);
    }

    @Test
    @DisplayName("GET /orders with valid with user's token - check for the latest user's order")
    public void getOrdersWithTokenTest(){
        OrderSteps steps = new OrderSteps();
        UserSteps user = new UserSteps();
        Response response = steps.getOrder(accessToken);
        user.assertStatusCode(response, SC_OK);
        user.compareSuccess(response, true);
        steps.compareOrderId(response, orderId);
    }

    @Test
    @DisplayName("GET /orders without user's token")
    public void getOrdersWithoutTokenTest(){
        OrderSteps steps = new OrderSteps();
        UserSteps user = new UserSteps();
        Response response = steps.getOrder("");
        user.assertStatusCode(response, SC_UNAUTHORIZED);
        user.compareSuccess(response, false);
        user.compareMessage(response, "You should be authorised");
    }

    @AfterClass
    public static void coolDown(){
        UserSteps user = new UserSteps();
        Response response = user.deleteUser(accessToken);
        user.assertStatusCode(response, SC_ACCEPTED);
    }
}
