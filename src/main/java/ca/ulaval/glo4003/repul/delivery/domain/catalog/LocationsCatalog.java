package ca.ulaval.glo4003.repul.delivery.domain.catalog;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationException;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;

public class LocationsCatalog {
    private final Map<DeliveryLocationId, DeliveryLocation> deliveryLocations;
    private final Map<KitchenLocationId, KitchenLocation> kitchenLocations;

    public LocationsCatalog(List<DeliveryLocation> deliveryLocations, List<KitchenLocation> kitchenLocations) {
        this.deliveryLocations = deliveryLocations.stream().collect(Collectors.toMap(DeliveryLocation::getLocationId, Function.identity()));
        this.kitchenLocations = kitchenLocations.stream().collect(Collectors.toMap(KitchenLocation::getLocationId, Function.identity()));
    }

    public DeliveryLocation getDeliveryLocation(DeliveryLocationId deliveryLocationId) {
        if (deliveryLocations.containsKey(deliveryLocationId)) {
            return deliveryLocations.get(deliveryLocationId);
        }
        throw new InvalidLocationException();
    }

    public KitchenLocation getKitchenLocation(KitchenLocationId kitchenLocationId) {
        if (kitchenLocations.containsKey(kitchenLocationId)) {
            return kitchenLocations.get(kitchenLocationId);
        }
        throw new InvalidLocationException();
    }

    public List<DeliveryLocation> getDeliveryLocations() {
        return deliveryLocations.values().stream().toList();
    }
}
