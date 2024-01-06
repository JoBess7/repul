package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

public class CargoFactory {
    public Cargo createCargo(KitchenLocation kitchenLocation, List<MealKit> mealKits) {
        return new Cargo(new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generate(), kitchenLocation, mealKits);
    }
}
