package ca.ulaval.glo4003.repul.user.api;

import java.net.URI;

import ca.ulaval.glo4003.repul.commons.api.UriFactory;
import ca.ulaval.glo4003.repul.user.api.assembler.UserResponseAssembler;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.user.application.UserService;
import ca.ulaval.glo4003.repul.user.application.query.LoginQuery;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserService userService;
    private final UserResponseAssembler userResponseAssembler = new UserResponseAssembler();
    private final UriFactory uriFactory = new UriFactory();

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Path("/users:register")
    public Response register(@NotNull(message = "The body must not be null.") @Valid RegistrationRequest registrationRequest) {
        RegistrationQuery registrationQuery =
            RegistrationQuery.from(registrationRequest.email, registrationRequest.password, registrationRequest.idul, registrationRequest.name,
                registrationRequest.birthdate, registrationRequest.gender);

        userService.register(registrationQuery);

        URI location = uriFactory.createURI("/api/users");
        return Response.created(location).build();
    }

    @POST
    @Path("/users:login")
    public Response login(@NotNull(message = "The body must not be null.") @Valid LoginRequest loginRequest) {
        LoginQuery loginQuery = LoginQuery.from(loginRequest.email, loginRequest.password);

        Token token = userService.login(loginQuery);
        LoginResponse loginResponse = userResponseAssembler.toLoginResponse(token);

        return Response.ok(loginResponse).build();
    }
}
