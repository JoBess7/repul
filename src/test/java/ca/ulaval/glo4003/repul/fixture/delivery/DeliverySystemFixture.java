package ca.ulaval.glo4003.repul.fixture.delivery;

import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;

public class DeliverySystemFixture {
    private LocationsCatalog locationsCatalog;

    public DeliverySystemFixture() {
        this.locationsCatalog = new LocationsCatalogFixture().build();
    }

    public DeliverySystemFixture withLocationsCatalog(LocationsCatalog locationsCatalog) {
        this.locationsCatalog = locationsCatalog;
        return this;
    }

    public DeliverySystem build() {
        return new DeliverySystem(locationsCatalog);
    }
}
