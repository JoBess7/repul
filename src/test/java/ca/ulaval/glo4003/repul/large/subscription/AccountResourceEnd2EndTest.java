package ca.ulaval.glo4003.repul.large.subscription;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.AddCardRequestFixture;
import ca.ulaval.glo4003.repul.fixture.user.LoginRequestFixture;
import ca.ulaval.glo4003.repul.fixture.user.RegistrationRequestFixture;
import ca.ulaval.glo4003.repul.subscription.api.request.AddCardRequest;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class AccountResourceEnd2EndTest {
    private static final String ACCOUNT_EMAIL = "myAccountEmail@ulaval.ca";
    private static final String ACCOUNT_PASSWORD = "secret123";
    private static final String ACCOUNT_IDUL = "almat69";
    private static final String ACCOUNT_NAME = "Bob Math";
    private static final String ACCOUNT_BIRTHDATE = "1969-04-20";
    private static final String ACCOUNT_GENDER = "MAN";
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
    public void givenExistingAccount_whenGettingAccount_shouldReturn200() {
        String accountToken = registerAndLogin();

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "accounts");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void givenExistingAccount_whenGettingAccount_shouldReturnAccountInformation() {
        String accountToken = registerAndLogin();

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "accounts");
        ProfilePayload responseBody = response.getBody().as(ProfilePayload.class);

        assertEquals(ACCOUNT_NAME, responseBody.name());
        assertEquals(ACCOUNT_BIRTHDATE, responseBody.birthdate());
        assertEquals(ACCOUNT_EMAIL, responseBody.email());
        assertEquals(ACCOUNT_GENDER, responseBody.gender());
        assertEquals(ACCOUNT_IDUL.toUpperCase(), responseBody.idul());
        assertNotEquals(0, responseBody.age());
    }

    @Test
    public void givenExistingAccount_whenAddingCard_shouldReturn204() {
        String accountToken = registerAndLogin();
        AddCardRequest addCardRequest = new AddCardRequestFixture().build();

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(addCardRequest)
            .post(CONTEXT.getURI() + "accounts:addCard");

        assertEquals(204, response.getStatusCode());
    }

    private String registerAndLogin() {
        RegistrationRequest registrationRequest = createRegistrationRequest();
        given().contentType(MediaType.APPLICATION_JSON).body(registrationRequest).post(CONTEXT.getURI() + "users:register");

        LoginRequest loginRequest = new LoginRequestFixture().withEmail(ACCOUNT_EMAIL).withPassword(ACCOUNT_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private RegistrationRequest createRegistrationRequest() {
        return new RegistrationRequestFixture().withEmail(ACCOUNT_EMAIL).withPassword(ACCOUNT_PASSWORD).withBirthdate(ACCOUNT_BIRTHDATE)
            .withGender(ACCOUNT_GENDER).withName(ACCOUNT_NAME).withIdul(ACCOUNT_IDUL).build();
    }
}
