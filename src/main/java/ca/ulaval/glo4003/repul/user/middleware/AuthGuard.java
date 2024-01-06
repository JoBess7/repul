package ca.ulaval.glo4003.repul.user.middleware;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserRepository;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.DecodedToken;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenDecoder;
import ca.ulaval.glo4003.repul.user.middleware.exception.MissingAuthorizationHeaderException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

@Secure
@Priority(Priorities.AUTHENTICATION)
public class AuthGuard implements ContainerRequestFilter {
    private final UserRepository userRepository;
    private final TokenDecoder tokenDecoder;

    private ResourceInfo resourceInfo;

    public AuthGuard(UserRepository userRepository, TokenDecoder tokenDecoder) {
        this.userRepository = userRepository;
        this.tokenDecoder = tokenDecoder;
    }

    @Context
    public void setResourceInfo(ResourceInfo resourceInfo) {
        this.resourceInfo = resourceInfo;
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            throw new MissingAuthorizationHeaderException();
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            DecodedToken decodedToken = tokenDecoder.decode(token);

            User user = userRepository.findByUid(decodedToken.userId());

            Method resourceMethod = resourceInfo.getResourceMethod();

            if (resourceMethod.isAnnotationPresent(Roles.class)) {
                validateRoles(resourceMethod.getAnnotation(Roles.class).value(), user.getRole(), decodedToken.role(), containerRequestContext);
            }

            containerRequestContext.setProperty("uid", decodedToken.userId().getUUID().toString());
        } catch (Exception e) {
            Response response = Response.status(Response.Status.UNAUTHORIZED).entity(Map.of("message", "Could not validate identity.")).build();
            containerRequestContext.abortWith(response);
        }
    }

    private void validateRoles(Role[] allowedRoles, Role actualRole, Role claimedRole, ContainerRequestContext containerRequestContext) {
        if (actualRole != claimedRole || !Arrays.stream(allowedRoles).toList().contains(claimedRole)) {
            Response response = Response.status(Response.Status.FORBIDDEN).entity(Map.of("message", "You do not have access to this resource.")).build();
            containerRequestContext.abortWith(response);
        }
    }
}
