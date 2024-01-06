package ca.ulaval.glo4003.repul.fixture.delivery;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;

public class LocationsCatalogFixture {
    private final Map<DeliveryLocationId, DeliveryLocation> deliveryLocations;
    private final Map<KitchenLocationId, KitchenLocation> kitchenLocations;

    public LocationsCatalogFixture() {
        this.deliveryLocations = new HashMap<>();
        this.kitchenLocations = new HashMap<>();
    }

    public LocationsCatalogFixture addDeliveryLocation(DeliveryLocation deliveryLocation) {
        deliveryLocations.put(deliveryLocation.getLocationId(), deliveryLocation);
        return this;
    }

    public LocationsCatalogFixture addKitchenLocation(KitchenLocation kitchenLocation) {
        kitchenLocations.put(kitchenLocation.getLocationId(), kitchenLocation);
        return this;
    }

    public LocationsCatalog build() {
        return new LocationsCatalog(deliveryLocations.values().stream().toList(), kitchenLocations.values().stream().toList());
    }
}
