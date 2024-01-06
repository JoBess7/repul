package ca.ulaval.glo4003.repul.delivery.api;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.api.assembler.CargosResponseAssembler;
import ca.ulaval.glo4003.repul.delivery.api.response.CargoResponse;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargosPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.LockerPayload;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.middleware.Roles;
import ca.ulaval.glo4003.repul.user.middleware.Secure;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class CargoResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private final DeliveryService deliveryService;
    private final CargosResponseAssembler cargosResponseAssembler = new CargosResponseAssembler();

    public CargoResource(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GET
    @Secure
    @Roles(Role.DELIVERY_PERSON)
    @Path("/cargos:toPickUp")
    public Response getCargosReadyToPickUp() {
        CargosPayload cargosPayload = deliveryService.getCargosReadyToPickUp();

        List<CargoResponse> cargosResponse = cargosResponseAssembler.toCargosResponse(cargosPayload);
        return Response.ok(cargosResponse).build();
    }

    @POST
    @Secure
    @Roles(Role.DELIVERY_PERSON)
    @Path("/cargos/{cargoId}:pickUp")
    public Response pickupCargo(@Context ContainerRequestContext context, @PathParam("cargoId") String cargoId) {
        DeliveryPersonUniqueIdentifier deliveryPersonId =
            new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom((String) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY));

        deliveryService.pickupCargo(deliveryPersonId, new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generateFrom(cargoId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.DELIVERY_PERSON)
    @Path("/cargos/{cargoId}:cancel")
    public Response cancelCargo(@Context ContainerRequestContext context, @PathParam("cargoId") String cargoId) {
        DeliveryPersonUniqueIdentifier deliveryPersonId = new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom(
            context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        deliveryService.cancelCargo(deliveryPersonId, new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generateFrom(cargoId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.DELIVERY_PERSON)
    @Path("/cargos/{cargoId}/mealKits/{mealKitId}:confirm")
    public Response confirmDelivery(@Context ContainerRequestContext context, @PathParam("cargoId") String cargoId, @PathParam("mealKitId") String mealKitId) {
        DeliveryPersonUniqueIdentifier deliveryPersonId =
            new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        deliveryService.confirmDelivery(deliveryPersonId, new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generateFrom(cargoId),
            new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(mealKitId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.DELIVERY_PERSON)
    @Path("/cargos/{cargoId}/mealKits/{mealKitId}:recall")
    public Response recallDelivery(@Context ContainerRequestContext context, @PathParam("cargoId") String cargoId, @PathParam("mealKitId") String mealKitId) {
        DeliveryPersonUniqueIdentifier deliveryPersonId =
            new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        LockerId lockerId = deliveryService.recallDelivery(deliveryPersonId, new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generateFrom(cargoId),
            new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(mealKitId));
        LockerPayload lockerPayload = LockerPayload.from(lockerId);

        return Response.ok(lockerPayload).build();
    }
}
