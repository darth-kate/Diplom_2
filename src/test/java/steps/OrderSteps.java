package steps;
import io.restassured.path.json.JsonPath;
import serial.*;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import urls.Urls;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class OrderSteps {
    private static String accessToken;
    @Step("Get ingredients")
    public Response getIngredients() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(Urls.GET_INGREDIENTS);
    }

    @Step("Extract ingredient from response to GET /api/ingredients")
    public String extractIngredient(Response response, int index){
        GetOrdersDeserial order = response.body().as(GetOrdersDeserial.class);
        return order.getData().get(index).get_id();
    }

    @Step("POST /orders")
    public Response postOrder(String accessToken, String[] ingredients){
        PostOrderSerial order = new PostOrderSerial(ingredients);
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .and()
                .body(order)
                .when()
                .post(Urls.POST_AND_GET_ORDER);
    }

    @Step("POST /orders")
    public Response getOrder(String accessToken){
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .when()
                .get(Urls.POST_AND_GET_ORDER);
    }

    @Step("Compare user's email in order with token")
    public void compareEmailInOrder(Response response, String email){
        JsonPath jsonPathEvaluator = response.jsonPath();
        String order_email = jsonPathEvaluator.get("order.owner.email");
        assertEquals(email, order_email);
    }

    @Step("Extract _id from response to POST /orders")
    public String extractOrderId(Response response){
        JsonPath jsonPathEvaluator = response.jsonPath();
        String orderId = jsonPathEvaluator.get("order._id");
        return orderId;
    }

    @Step("Compare orderId, which was posted, with one of orders")
    public void compareOrderId(Response response, String orderId){
        GetUsersOrdersDeserial order = response.body().as(GetUsersOrdersDeserial.class);
        List<OrdersDeserial> orders = order.getOrders();
        String user_orderId = orders.get(orders.size() - 1).get_id();
        assertEquals(orderId, user_orderId);
    }

}
