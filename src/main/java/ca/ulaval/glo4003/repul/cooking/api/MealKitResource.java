package ca.ulaval.glo4003.repul.cooking.api;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.api.assembler.MealKitsResponseAssembler;
import ca.ulaval.glo4003.repul.cooking.api.request.ConfirmCookedRequest;
import ca.ulaval.glo4003.repul.cooking.api.request.SelectionRequest;
import ca.ulaval.glo4003.repul.cooking.api.response.SelectionResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.ToCookResponse;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.middleware.Roles;
import ca.ulaval.glo4003.repul.user.middleware.Secure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
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
@Consumes(MediaType.APPLICATION_JSON)
public class MealKitResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";

    private final CookingService cookingService;
    private final MealKitsResponseAssembler mealKitsResponseAssembler = new MealKitsResponseAssembler();

    public MealKitResource(CookingService cookingService) {
        this.cookingService = cookingService;
    }

    @GET
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:toCook")
    public Response getMealKitsToCook() {
        MealKitsPayload mealKitsPayload = cookingService.getMealKitsToCook();

        ToCookResponse toCookResponse = mealKitsResponseAssembler.toToCookResponse(mealKitsPayload);

        return Response.ok(toCookResponse).build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:select")
    public Response select(@Context ContainerRequestContext context,
                           @NotNull(message = "The body must not be null.") @Valid SelectionRequest selectionRequest) {
        CookUniqueIdentifier cookId =
            new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        cookingService.select(cookId, new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(selectionRequest.ids));

        return Response.noContent().build();
    }

    @GET
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:getSelection")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelection(@Context ContainerRequestContext context) {
        CookUniqueIdentifier cookId =
            new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        List<MealKitUniqueIdentifier> mealKitIds = cookingService.getSelection(cookId);

        SelectionResponse selectionResponse = mealKitsResponseAssembler.toSelectionResponse(mealKitIds);
        return Response.ok(selectionResponse).build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits/{mealKitId}:cancelSelection")
    public Response cancelOneSelection(@Context ContainerRequestContext context, @PathParam("mealKitId") String mealKitId) {
        CookUniqueIdentifier cookId =
            new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        cookingService.cancelOneSelected(cookId, new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(mealKitId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:cancelSelection")
    public Response cancelAllSelection(@Context ContainerRequestContext context) {
        CookUniqueIdentifier cookId =
            new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        cookingService.cancelAllSelected(cookId);

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits/{mealKitId}:confirmCooked")
    public Response confirmCooked(@Context ContainerRequestContext context, @PathParam("mealKitId") String mealKitId) {
        CookUniqueIdentifier cookId =
            new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        cookingService.confirmCooked(cookId, new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(mealKitId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:confirmCooked")
    public Response confirmCooked(@Context ContainerRequestContext context,
                                  @NotNull(message = "The body must not be null.") @Valid ConfirmCookedRequest confirmCookedRequest) {
        CookUniqueIdentifier cookId =
            new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        cookingService.confirmCooked(cookId, new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(confirmCookedRequest.ids));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits/{mealKitId}:recallCooked")
    public Response recallCooked(@Context ContainerRequestContext context, @PathParam("mealKitId") String mealKitId) {
        CookUniqueIdentifier cookId =
            new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        cookingService.recallCooked(cookId, new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(mealKitId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.CLIENT)
    @Path("/mealKits/{mealKitId}:pickUp")
    public Response pickupMealKit(@Context ContainerRequestContext context, @PathParam("mealKitId") String mealKitId) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom((String) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY));

        cookingService.pickupNonDeliverableMealKit(subscriberId, new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(mealKitId));

        return Response.noContent().build();
    }
}
