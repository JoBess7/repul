package ca.ulaval.glo4003.repul.small.user.middleware;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserRepository;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.TokenVerificationFailedException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.UserNotFoundException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.DecodedToken;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenDecoder;
import ca.ulaval.glo4003.repul.user.middleware.AuthGuard;
import ca.ulaval.glo4003.repul.user.middleware.Roles;
import ca.ulaval.glo4003.repul.user.middleware.exception.MissingAuthorizationHeaderException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthGuardTest {
    private static final SubscriberUniqueIdentifier SUBSCRIBER_UNIQUE_IDENTIFIER = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final String A_UID_STRING = SUBSCRIBER_UNIQUE_IDENTIFIER.getUUID().toString();
    private static final String A_TOKEN = "valid-token-yolo";
    private static final DecodedToken A_DECODED_TOKEN = new DecodedToken(SUBSCRIBER_UNIQUE_IDENTIFIER, Role.CLIENT);
    private static final String A_NOT_BEARER_AUTHORIZATION_HEADER = "Ours uehriwhriehwiuhriwhr";
    private static final String A_VALID_BEARER_AUTHORIZATION_HEADER = "Bearer " + A_TOKEN;
    private static final String AN_INVALID_BEARER_AUTHORIZATION_HEADER = "Bearer " + A_TOKEN + "-invalid";

    private AuthGuard authGuard;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenDecoder tokenDecoder;

    @Mock
    private ContainerRequestContext containerRequestContext;

    @Mock
    private ResourceInfo resourceInfo;

    @BeforeEach
    public void createAuthGuard() {
        authGuard = new AuthGuard(userRepository, tokenDecoder);
        authGuard.setResourceInfo(resourceInfo);
    }

    @Test
    public void givenNoAuthorizationHeader_whenFilter_shouldThrowMissingAuthorizationHeaderException() {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(null);

        assertThrows(MissingAuthorizationHeaderException.class, () -> {
            authGuard.filter(containerRequestContext);
        });
    }

    @Test
    public void givenAuthorizationHeaderWithoutBearer_whenFilter_shouldThrowMissingAuthorizationHeaderException() {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_NOT_BEARER_AUTHORIZATION_HEADER);

        assertThrows(MissingAuthorizationHeaderException.class, () -> {
            authGuard.filter(containerRequestContext);
        });
    }

    @Test
    public void givenBearerToken_whenFilter_shouldDecodeToken() throws IOException {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_VALID_BEARER_AUTHORIZATION_HEADER);
        given(tokenDecoder.decode(A_TOKEN)).willReturn(A_DECODED_TOKEN);

        authGuard.filter(containerRequestContext);

        verify(tokenDecoder).decode(A_TOKEN);
    }

    @Test
    public void givenInvalidBearerToken_whenFilter_shouldAbortRequest() throws IOException {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(AN_INVALID_BEARER_AUTHORIZATION_HEADER);
        given(tokenDecoder.decode(A_TOKEN)).willThrow(new TokenVerificationFailedException());

        authGuard.filter(containerRequestContext);

        verify(containerRequestContext).abortWith(any());
    }

    @Test
    public void givenBearerToken_whenFilter_shouldCheckUid() throws IOException {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_VALID_BEARER_AUTHORIZATION_HEADER);
        given(userRepository.findByUid(SUBSCRIBER_UNIQUE_IDENTIFIER)).willReturn(mock(User.class));
        given(tokenDecoder.decode(A_TOKEN)).willReturn(A_DECODED_TOKEN);

        authGuard.filter(containerRequestContext);

        verify(tokenDecoder).decode(A_TOKEN);
    }

    @Test
    public void givenBearerTokenWithInvalidUid_whenFilter_shouldAbortRequest() throws IOException {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_VALID_BEARER_AUTHORIZATION_HEADER);
        given(userRepository.findByUid(SUBSCRIBER_UNIQUE_IDENTIFIER)).willThrow(new UserNotFoundException());
        given(tokenDecoder.decode(A_TOKEN)).willReturn(A_DECODED_TOKEN);

        authGuard.filter(containerRequestContext);

        verify(containerRequestContext).abortWith(any());
    }

    @Test
    public void givenRolesAnnotationAndWrongRole_whenFilter_shouldAbortRequest() throws IOException, NoSuchMethodException, SecurityException {
        User user = mockUserWithRole(Role.ADMIN);
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_VALID_BEARER_AUTHORIZATION_HEADER);
        given(userRepository.findByUid(SUBSCRIBER_UNIQUE_IDENTIFIER)).willReturn(user);
        given(tokenDecoder.decode(A_TOKEN)).willReturn(A_DECODED_TOKEN);
        Method mockedMethod = AuthGuardTest.class.getMethod("clientRoleMethod");
        given(resourceInfo.getResourceMethod()).willReturn(mockedMethod);

        authGuard.filter(containerRequestContext);

        verify(containerRequestContext).abortWith(any());
    }

    @Test
    public void givenRolesAnnotationAndAuthorizedRole_whenFilter_shouldNotAbortRequest() throws IOException, NoSuchMethodException, SecurityException {
        User user = mockUserWithRole(Role.CLIENT);
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_VALID_BEARER_AUTHORIZATION_HEADER);
        given(userRepository.findByUid(SUBSCRIBER_UNIQUE_IDENTIFIER)).willReturn(user);
        given(tokenDecoder.decode(A_TOKEN)).willReturn(A_DECODED_TOKEN);
        Method mockedMethod = AuthGuardTest.class.getMethod("clientRoleMethod");
        given(resourceInfo.getResourceMethod()).willReturn(mockedMethod);

        authGuard.filter(containerRequestContext);

        verify(containerRequestContext).setProperty("uid", A_UID_STRING);
    }

    private User mockUserWithRole(Role role) {
        User user = mock(User.class);
        given(user.getRole()).willReturn(role);
        return user;
    }

    @Roles(Role.CLIENT)
    public void clientRoleMethod() {
        // For annotation test purposes only
    }
}
