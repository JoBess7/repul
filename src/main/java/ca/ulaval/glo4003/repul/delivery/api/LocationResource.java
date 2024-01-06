package ca.ulaval.glo4003.repul.delivery.api;

import java.util.List;

import ca.ulaval.glo4003.repul.delivery.api.assembler.LocationsCatalogResponseAssembler;
import ca.ulaval.glo4003.repul.delivery.api.response.DeliveryLocationResponse;
import ca.ulaval.glo4003.repul.delivery.application.LocationsCatalogService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/locations")
public class LocationResource {
    private final LocationsCatalogService locationsCatalogService;
    private final LocationsCatalogResponseAssembler locationsCatalogResponseAssembler = new LocationsCatalogResponseAssembler();

    public LocationResource(LocationsCatalogService locationsCatalogService) {
        this.locationsCatalogService = locationsCatalogService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeliveryLocations() {
        List<DeliveryLocationResponse> locations = locationsCatalogResponseAssembler.toLocationsResponse(locationsCatalogService.getDeliveryLocations());
        return Response.ok(locations).build();
    }
}
