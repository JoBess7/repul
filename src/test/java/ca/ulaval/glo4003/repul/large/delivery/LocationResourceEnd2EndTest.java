package ca.ulaval.glo4003.repul.large.delivery;

import java.util.Arrays;
import java.util.List;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.delivery.api.response.DeliveryLocationResponse;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocationResourceEnd2EndTest {
    private static final ApplicationContext CONTEXT = new TestApplicationContext();
    private static final DeliveryLocationResponse EXPECTED_LOCATION = new DeliveryLocationResponse("MYRAND", "Secteur EST", 12);

    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        server = new ServerFixture(CONTEXT);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        server.stop();
    }

    @Test
    public void whenGettingLocations_shouldReturn200() {
        Response response = when().get(CONTEXT.getURI() + "locations");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingLocations_shouldReturnLocations() {
        Response response = when().get(CONTEXT.getURI() + "locations");
        List<DeliveryLocationResponse> actualBody = Arrays.asList(response.getBody().as(DeliveryLocationResponse[].class));

        assertFalse(actualBody.isEmpty());
        assertTrue(actualBody.contains(EXPECTED_LOCATION));
    }
}
