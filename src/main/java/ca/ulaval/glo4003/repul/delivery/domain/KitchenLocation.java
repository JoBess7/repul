package ca.ulaval.glo4003.repul.delivery.domain;

import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;

public class KitchenLocation {
    private final KitchenLocationId kitchenLocationId;
    private final String name;

    public KitchenLocation(KitchenLocationId kitchenLocationId, String name) {
        this.kitchenLocationId = kitchenLocationId;
        this.name = name;
    }

    public KitchenLocationId getLocationId() {
        return kitchenLocationId;
    }
}
