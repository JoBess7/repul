package ca.ulaval.glo4003.repul.lockerauthorization.middleware;

import java.util.Map;

import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.user.middleware.exception.MissingAuthorizationHeaderException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

@ApiKey
@Priority(Priorities.AUTHENTICATION)
public class ApiKeyGuard implements ContainerRequestFilter {
    private final EnvParser envParser = EnvParser.getInstance();

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            throw new MissingAuthorizationHeaderException();
        }

        String apiKey = authorizationHeader.substring("Bearer".length()).trim();

        String validApiKey = envParser.readVariable("LOCKER_API_KEY");

        if (!validApiKey.equals(apiKey)) {
            Response response = Response.status(Response.Status.UNAUTHORIZED).entity(
                Map.of("message", "Invalid API key.")).build();
            containerRequestContext.abortWith(response);
        }
    }
}
