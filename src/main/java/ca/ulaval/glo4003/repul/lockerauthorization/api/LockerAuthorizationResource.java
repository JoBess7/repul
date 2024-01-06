package ca.ulaval.glo4003.repul.lockerauthorization.api;

import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.api.request.OpenLockerRequest;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.middleware.ApiKey;

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
public class LockerAuthorizationResource {
    private final LockerAuthorizationService lockerAuthorizationService;

    public LockerAuthorizationResource(LockerAuthorizationService lockerAuthorizationService) {
        this.lockerAuthorizationService = lockerAuthorizationService;
    }

    @POST
    @ApiKey
    @Path("/locker:open")
    public Response openLocker(@NotNull(message = "The body must not be null.") @Valid OpenLockerRequest openLockerRequest) {
        OpenLockerQuery query = OpenLockerQuery.from(
            openLockerRequest.userCardNumber,
            openLockerRequest.lockerId);

        lockerAuthorizationService.openLocker(query);

        return Response.noContent().build();
    }
}
