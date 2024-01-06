package ca.ulaval.glo4003.repul.small.lockerauthorization.middleware;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.lockerauthorization.middleware.ApiKeyGuard;
import ca.ulaval.glo4003.repul.user.middleware.exception.MissingAuthorizationHeaderException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ApiKeyGuardTest {
    private static final String A_NOT_BEARER_AUTHORIZATION_HEADER = "Ours uehriwhriehwiuhriwhr";
    private static final String AN_INVALID_BEARER_API_KEY_HEADER = "Bearer " + "invalid-api-key";

    private ApiKeyGuard apiKeyGuard;

    @Mock
    private ContainerRequestContext containerRequestContext;

    @BeforeEach
    public void createApiKeyGuard() {
        apiKeyGuard = new ApiKeyGuard();
    }

    @Test
    public void givenNoAuthorizationHeader_whenFilter_shouldThrowMissingAuthorizationHeaderException() {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(null);

        assertThrows(MissingAuthorizationHeaderException.class, () -> {
            apiKeyGuard.filter(containerRequestContext);
        });
    }

    @Test
    public void givenAuthorizationHeaderWithoutBearer_whenFilter_shouldThrowMissingAuthorizationHeaderException() {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_NOT_BEARER_AUTHORIZATION_HEADER);

        assertThrows(MissingAuthorizationHeaderException.class, () -> {
            apiKeyGuard.filter(containerRequestContext);
        });
    }

    @Test
    public void givenAuthorizationHeaderWithInvalidApiKey_whenFilter_shouldAbortRequest() {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(AN_INVALID_BEARER_API_KEY_HEADER);

        apiKeyGuard.filter(containerRequestContext);

        verify(containerRequestContext).abortWith(any());
    }
}
