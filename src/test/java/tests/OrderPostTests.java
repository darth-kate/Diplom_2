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

public class OrderPostTests {
    private static String accessToken;
    private static String ingredientOne;
    private static String ingredientTwo;
    private static String ingredientThree;
    private static UserRegisterSerial user;


    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = Urls.URL;
        UserSteps userSteps = new UserSteps();
        OrderSteps orderSteps = new OrderSteps();

        user = UserRegisterSerial.builder()
                .email(userSteps.generateString() + "@yandex.ru")
                .password("test_password")
                .name("Ivan")
                .build();
        Response registerUser = userSteps.registerUser(user.getEmail(), user.getPassword(), user.getName());
        userSteps.assertStatusCode(registerUser, SC_OK);

        Response loginUser = userSteps.loginUser(user.getEmail(), user.getPassword());
        accessToken = userSteps.extractToken(loginUser);

        Response response  = orderSteps.getIngredients();
        ingredientOne = orderSteps.extractIngredient(response, 0);
        ingredientTwo = orderSteps.extractIngredient(response, 1);
        ingredientThree = orderSteps.extractIngredient(response, 2);
    }

    @Test
    @DisplayName("POST /orders with valid ingredients without token")
    public void postValidOrderWithoutTokenTest(){
        OrderSteps steps = new OrderSteps();
        UserSteps user = new UserSteps();
        Response response = steps.postOrder("", new String[]{ingredientOne, ingredientTwo, ingredientThree});
        user.assertStatusCode(response, SC_OK);
        user.compareSuccess(response, true);
    }

    @Test
    @DisplayName("POST /orders with valid ingredients with token")
    public void postValidOrderWithTokenTest(){
        OrderSteps orderSteps = new OrderSteps();
        UserSteps userSteps = new UserSteps();
        Response response = orderSteps.postOrder(accessToken, new String[]{ingredientOne, ingredientTwo, ingredientThree});
        userSteps.assertStatusCode(response, SC_OK);
        userSteps.compareSuccess(response, true);
        orderSteps.compareEmailInOrder(response, user.getEmail());
    }

    @Test
    @DisplayName("POST /orders without ingredients")
    public void postOrderWithoutIngredientTest(){
        OrderSteps steps = new OrderSteps();
        UserSteps user = new UserSteps();
        Response response = steps.postOrder("", new String[]{});
        user.assertStatusCode(response, SC_BAD_REQUEST);
        user.compareMessage(response, "Ingredient ids must be provided");
        user.compareSuccess(response, false);
    }

    @Test
    @DisplayName("POST /orders with invalid ingredient")
    public void postOrderWithInvalidIngredientTest(){
        OrderSteps steps = new OrderSteps();
        UserSteps user = new UserSteps();
        String ingredientOne = "1";
        Response response = steps.postOrder("", new String[]{ingredientOne});
        user.assertStatusCode(response, SC_INTERNAL_SERVER_ERROR);
    }

    @AfterClass
    public static void coolDown(){
        if (accessToken == null) {
            return;
        }
        UserSteps user = new UserSteps();
        Response response = user.deleteUser(accessToken);
        user.assertStatusCode(response, SC_ACCEPTED);
        accessToken = null;
    }

}
