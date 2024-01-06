package ca.ulaval.glo4003.repul.small.user.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.fixture.user.LoginRequestFixture;
import ca.ulaval.glo4003.repul.fixture.user.RegistrationRequestFixture;
import ca.ulaval.glo4003.repul.user.api.UserResource;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.user.application.UserService;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserResourceTest {
    private static final RegistrationRequest A_REGISTRATION_REQUEST = new RegistrationRequestFixture().build();
    private static final LoginRequest A_LOGIN_REQUEST = new LoginRequestFixture().build();
    private static final Token A_TOKEN = new Token("aToken", 3600);

    private UserResource userResource;

    @Mock
    private UserService userService;

    @BeforeEach
    public void createUserResource() {
        userResource = new UserResource(userService);
    }

    @Test
    public void whenRegistering_shouldReturn201() {
        Response response = userResource.register(A_REGISTRATION_REQUEST);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenRegistering_shouldHaveLocationHeader() {
        Response response = userResource.register(A_REGISTRATION_REQUEST);

        assertNotNull(response.getHeaderString("Location"));
    }

    @Test
    public void whenLogin_shouldReturn200() {
        given(userService.login(any())).willReturn(A_TOKEN);

        Response response = userResource.login(A_LOGIN_REQUEST);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenLogin_shouldReturnTokenAndExpiresIn() {
        given(userService.login(any())).willReturn(A_TOKEN);

        Response response = userResource.login(A_LOGIN_REQUEST);
        LoginResponse loginResponse = (LoginResponse) response.getEntity();

        assertEquals(A_TOKEN.value(), loginResponse.token());
        assertEquals(A_TOKEN.expiresIn(), loginResponse.expiresIn());
    }
}
