package ca.ulaval.glo4003.repul.large.cooking;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.cooking.api.request.ConfirmCookedRequest;
import ca.ulaval.glo4003.repul.cooking.api.request.SelectionRequest;
import ca.ulaval.glo4003.repul.cooking.api.response.SelectionResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.ToCookResponse;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.user.LoginRequestFixture;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MealKitResourceEnd2EndTest {
    private static final ApplicationContext CONTEXT = new TestApplicationContext();
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        RestAssured.urlEncodingEnabled = false;
        System.setOut(new PrintStream(outputStream));
        server = new ServerFixture(CONTEXT);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        loginAndCancelAllSelection();
        RestAssured.reset();
        System.setOut(System.out);
        server.stop();
    }

    @Test
    public void whenGettingMealKitsToCook_shouldReturn200() {
        String accountToken = loginAsACook();

        Response response = given().contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "mealKits:toCook");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingMealKitsToCook_shouldReturnMealKitsToCook() {
        String accountToken = loginAsACook();

        Response response = given().contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "mealKits:toCook");
        ToCookResponse responseBody = response.getBody().as(ToCookResponse.class);

        assertFalse(responseBody.mealKits().isEmpty());
        assertFalse(responseBody.totalIngredients().isEmpty());
    }

    @Test
    public void whenSelectingMealKitsToCook_shouldReturn204() {
        String accountToken = loginAsACook();
        SelectionRequest selectionRequest = new SelectionRequest();
        selectionRequest.ids = List.of(TestApplicationContext.FIRST_MEAL_KIT_ID.getUUID().toString());

        Response response = given().contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accountToken).body(selectionRequest)
            .post(CONTEXT.getURI() + "mealKits:select");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenGettingSelection_shouldReturn200() {
        String accountToken = loginAsACook();
        selectMealKitsToCook(accountToken, List.of(TestApplicationContext.FIRST_MEAL_KIT_ID.getUUID().toString()));

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "mealKits:getSelection");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingSelection_shouldReturnSelection() {
        String accountToken = loginAsACook();
        selectMealKitsToCook(accountToken, List.of(TestApplicationContext.FIRST_MEAL_KIT_ID.getUUID().toString()));

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "mealKits:getSelection");
        SelectionResponse responseBody = response.getBody().as(SelectionResponse.class);

        assertFalse(responseBody.mealKitSelectionIds().isEmpty());
        assertEquals(TestApplicationContext.FIRST_MEAL_KIT_ID.getUUID().toString(), responseBody.mealKitSelectionIds().get(0));
    }

    @Test
    public void whenCancelingOneSelection_shouldReturn204() {
        String accountToken = loginAsACook();
        selectMealKitsToCook(accountToken, List.of(TestApplicationContext.FIRST_MEAL_KIT_ID.getUUID().toString()));

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "mealKits/" + TestApplicationContext.FIRST_MEAL_KIT_ID.getUUID() + ":cancelSelection");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenCancelingCompleteSelection_shouldReturn204() {
        String accountToken = loginAsACook();
        selectMealKitsToCook(accountToken,
            List.of(TestApplicationContext.FIRST_MEAL_KIT_ID.getUUID().toString(), TestApplicationContext.SECOND_MEAL_KIT_ID.getUUID().toString()));

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
                .post(CONTEXT.getURI() + "mealKits:cancelSelection");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenConfirmingOneCooked_shouldReturn204() {
        String accountToken = loginAsACook();
        selectMealKitsToCook(accountToken, List.of(TestApplicationContext.SECOND_MEAL_KIT_ID.getUUID().toString()));

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "mealKits/" + TestApplicationContext.SECOND_MEAL_KIT_ID.getUUID() + ":confirmCooked");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenConfirmingOneCooked_shouldNotifyDeliveryMan() {
        String accountToken = loginAsACook();
        selectMealKitsToCook(accountToken, List.of(TestApplicationContext.THIRD_MEAL_KIT_ID.getUUID().toString()));

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "mealKits/" + TestApplicationContext.THIRD_MEAL_KIT_ID.getUUID() + ":confirmCooked");
        String sentMessage = outputStream.toString().trim();

        assertTrue(sentMessage.contains("Sending notification to: " + TestApplicationContext.DELIVERY_PERSON_EMAIL));
    }

    @Test
    public void whenConfirmingMultipleCooked_shouldReturn204() {
        String accountToken = loginAsACook();
        selectMealKitsToCook(accountToken,
            List.of(TestApplicationContext.FOURTH_MEAL_KIT_ID.getUUID().toString(), TestApplicationContext.FIFTH_MEAL_KIT_ID.getUUID().toString()));
        ConfirmCookedRequest confirmCookedRequest = new ConfirmCookedRequest();
        confirmCookedRequest.ids =
            List.of(TestApplicationContext.FOURTH_MEAL_KIT_ID.getUUID().toString(), TestApplicationContext.FIFTH_MEAL_KIT_ID.getUUID().toString());

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(confirmCookedRequest)
            .post(CONTEXT.getURI() + "mealKits:confirmCooked");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenConfirmingMultipleCooked_shouldNotifyDeliveryMan() {
        String accountToken = loginAsACook();
        selectMealKitsToCook(accountToken,
            List.of(TestApplicationContext.SIXTH_MEAL_KIT_ID.getUUID().toString(), TestApplicationContext.SEVENTH_MEAL_KIT_ID.getUUID().toString()));
        ConfirmCookedRequest confirmCookedRequest = new ConfirmCookedRequest();
        confirmCookedRequest.ids =
            List.of(TestApplicationContext.SIXTH_MEAL_KIT_ID.getUUID().toString(), TestApplicationContext.SEVENTH_MEAL_KIT_ID.getUUID().toString());

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(confirmCookedRequest)
            .post(CONTEXT.getURI() + "mealKits:confirmCooked");
        String sentMessage = outputStream.toString().trim();

        assertTrue(sentMessage.contains("Sending notification to: " + TestApplicationContext.DELIVERY_PERSON_EMAIL));
    }

    @Test
    public void whenRecallingOneCooked_shouldReturn204() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToCook(accountToken, List.of(TestApplicationContext.EIGHTH_MEAL_KIT_ID.getUUID().toString()));

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "mealKits/" + TestApplicationContext.EIGHTH_MEAL_KIT_ID.getUUID() + ":recallCooked");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenRecallingOneCooked_shouldPutItBackInRequestingCookSelection() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToCook(accountToken, List.of(TestApplicationContext.NINTH_MEAL_KIT_ID.getUUID().toString()));

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "mealKits/" + TestApplicationContext.NINTH_MEAL_KIT_ID.getUUID() + ":recallCooked");

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "mealKits:getSelection");
        SelectionResponse responseBody = response.getBody().as(SelectionResponse.class);
        assertFalse(responseBody.mealKitSelectionIds().isEmpty());
        assertEquals(TestApplicationContext.NINTH_MEAL_KIT_ID.getUUID().toString(), responseBody.mealKitSelectionIds().get(0));
    }

    @Test
    public void whenPickingUpMealKit_shouldReturn204() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToCook(accountToken, List.of(TestApplicationContext.NINTH_MEAL_KIT_ID.getUUID().toString()));
        String userAccountToken = loginAsUser();

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + userAccountToken)
            .post(CONTEXT.getURI() + "mealKits/" + TestApplicationContext.NINTH_MEAL_KIT_ID.getUUID() + ":pickUp");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenPickingUpMealKit_shouldBeRemovedFromKitchenAndSoNotRecallableAnymore() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToCook(accountToken, List.of(TestApplicationContext.NINTH_MEAL_KIT_ID.getUUID().toString()));
        String userAccountToken = loginAsUser();

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + userAccountToken)
            .post(CONTEXT.getURI() + "mealKits/" + TestApplicationContext.NINTH_MEAL_KIT_ID.getUUID() + ":pickUp");

        Response recallResponse = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "mealKits/" + TestApplicationContext.NINTH_MEAL_KIT_ID.getUUID() + ":recallCooked");
        assertEquals(404, recallResponse.getStatusCode());
    }

    private String loginAsACook() {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestApplicationContext.COOK_EMAIL).withPassword(TestApplicationContext.COOK_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private void loginAndCancelAllSelection() {
        String accountToken = loginAsACook();
        Response response = given().contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accountToken).post(CONTEXT.getURI() + "mealKits:cancelSelection");

        assertEquals(204, response.getStatusCode());
    }

    private void selectMealKitsToCook(String accountToken, List<String> mealKitIds) {
        SelectionRequest selectionRequest = new SelectionRequest();
        selectionRequest.ids = mealKitIds;

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(selectionRequest)
            .post(CONTEXT.getURI() + "mealKits:select");
    }

    private void selectAndConfirmMealKitsToCook(String accountToken, List<String> mealKitIds) {
        selectMealKitsToCook(accountToken, mealKitIds);

        ConfirmCookedRequest confirmCookedRequest = new ConfirmCookedRequest();
        confirmCookedRequest.ids = mealKitIds;

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(confirmCookedRequest)
            .post(CONTEXT.getURI() + "mealKits:confirmCooked");
    }

    private String loginAsUser() {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestApplicationContext.CLIENT_EMAIL).withPassword(TestApplicationContext.CLIENT_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }
}
