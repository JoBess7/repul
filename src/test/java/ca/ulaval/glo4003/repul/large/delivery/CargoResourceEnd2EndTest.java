package ca.ulaval.glo4003.repul.large.delivery;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.delivery.api.response.CargoResponse;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.user.LoginRequestFixture;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CargoResourceEnd2EndTest {
    private static final ApplicationContext CONTEXT = new TestApplicationContext();

    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        RestAssured.urlEncodingEnabled = false;
        server = new ServerFixture(CONTEXT);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        RestAssured.reset();
        server.stop();
    }

    @Test
    public void whenGettingCargosReadyToPickUp_shouldReturn200() {
        String accountToken = loginAsADeliveryPerson();

        Response response = given().header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "cargos:toPickUp");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingCargosReadyToPickUp_shouldReturnCargosAvailableForPickup() {
        String accountToken = loginAsADeliveryPerson();

        Response response = given().header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "cargos:toPickUp");
        List<CargoResponse> actualResponse = Arrays.asList(response.getBody().as(CargoResponse[].class));
        Optional<CargoResponse> actualCargo = actualResponse.stream().findFirst();

        assertTrue(actualCargo.isPresent());
        assertFalse(actualCargo.get().mealKitsResponse().isEmpty());
    }

    @Test
    public void whenPickingUpCargo_shouldReturn204() {
        String accountToken = loginAsADeliveryPerson();
        String cargoId = getCargoIdAvailableForPickup(accountToken);

        Response response = given().header("Authorization", "Bearer " + accountToken).post(CONTEXT.getURI() + "cargos/" + cargoId + ":pickUp");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenCancelPickUp_shouldReturn204() {
        String accountToken = loginAsADeliveryPerson();
        String cargoId = getCargoIdAvailableForPickup(accountToken);
        pickupCargo(accountToken, cargoId);

        Response response = given().header("Authorization", "Bearer " + accountToken).post(CONTEXT.getURI() + "cargos/" + cargoId + ":cancel");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void givenDifferentDeliveryPerson_whenCancelPickUp_shouldReturn400() {
        String accountToken = loginAsADeliveryPerson();
        String cargoId = getCargoIdAvailableForPickup(accountToken);
        pickupCargo(accountToken, cargoId);
        String anotherAccountToken = loginAsADifferentDeliveryPerson();

        Response response = given().header("Authorization", "Bearer " + anotherAccountToken).post(CONTEXT.getURI() + "cargos/" + cargoId + ":cancel");

        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void whenConfirmingDelivery_shouldReturn204() {
        String accountToken = loginAsADeliveryPerson();
        String cargoId = getCargoIdAvailableForPickup(accountToken);
        pickupCargo(accountToken, cargoId);

        Response response = given().header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "cargos/" + cargoId + "/mealKits/" + TestApplicationContext.TENTH_MEAL_KIT_ID.getUUID().toString() + ":confirm");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenRecallingMealKitInCargo_shouldReturn200() {
        String accountToken = loginAsADeliveryPerson();
        String cargoId = getCargoIdAvailableForPickup(accountToken);
        pickupCargo(accountToken, cargoId);
        confirmDelivery(accountToken, cargoId);

        Response response = given().header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "cargos/" + cargoId + "/mealKits/" + TestApplicationContext.TENTH_MEAL_KIT_ID.getUUID().toString() + ":recall");

        assertEquals(200, response.getStatusCode());
    }

    private String loginAsADeliveryPerson() {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestApplicationContext.DELIVERY_PERSON_EMAIL).withPassword(TestApplicationContext.DELIVERY_PERSON_PASSWORD)
                .build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).when().post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private String loginAsADifferentDeliveryPerson() {
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(TestApplicationContext.SECOND_DELIVERY_PERSON_EMAIL)
            .withPassword(TestApplicationContext.SECOND_DELIVERY_PERSON_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private String getCargoIdAvailableForPickup(String accountToken) {
        Response response = given().header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "cargos:toPickUp");
        List<CargoResponse> actualResponse = Arrays.asList(response.getBody().as(CargoResponse[].class));

        return actualResponse.stream().findFirst().get().cargoId();
    }

    private void pickupCargo(String accountToken, String cargoId) {
        given().header("Authorization", "Bearer " + accountToken).post(CONTEXT.getURI() + "cargos/" + cargoId + ":pickUp");
    }

    private void confirmDelivery(String accountToken, String cargoId) {
        given().header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "cargos/" + cargoId + "/mealKits/" + TestApplicationContext.TENTH_MEAL_KIT_ID.getUUID().toString() + ":confirm");
    }
}
