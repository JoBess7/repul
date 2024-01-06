package ca.ulaval.glo4003.repul.small.health.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.health.api.HealthResource;
import ca.ulaval.glo4003.repul.health.api.dto.HealthDto;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HealthResourceTest {
    private HealthResource healthResource;

    @BeforeEach
    public void createHealthResource() {
        healthResource = new HealthResource();
    }

    @Test
    public void whenGettingHealth_shouldReturnHealthDTO() {
        HealthDto expectedDto = new HealthDto("OK - Everything is alright!");

        Response response = healthResource.getHealth();

        assertEquals(expectedDto, response.getEntity());
    }

    @Test
    public void whenGettingHealth_shouldReturn200() {
        Response response = healthResource.getHealth();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
