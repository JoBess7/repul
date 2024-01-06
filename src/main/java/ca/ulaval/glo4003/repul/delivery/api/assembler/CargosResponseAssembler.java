package ca.ulaval.glo4003.repul.delivery.api.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.delivery.api.response.CargoResponse;
import ca.ulaval.glo4003.repul.delivery.api.response.MealKitResponse;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargoPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargosPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.MealKitPayload;

public class CargosResponseAssembler {

    public List<CargoResponse> toCargosResponse(CargosPayload cargosPayload) {
        return cargosPayload.cargoPayloads().stream().map(this::toCargoResponse).toList();
    }

    private CargoResponse toCargoResponse(CargoPayload cargoPayload) {
        List<MealKitResponse> mealKitResponses = cargoPayload.mealKitsPayload().stream().map(this::toMealKitResponse).toList();
        return new CargoResponse(
            cargoPayload.cargoId().getUUID().toString(),
            cargoPayload.kitchenLocationId(),
            mealKitResponses
        );
    }

    private MealKitResponse toMealKitResponse(MealKitPayload mealKitPayload) {
        return new MealKitResponse(
            mealKitPayload.mealKitId().getUUID().toString(),
            mealKitPayload.deliveryLocationId(),
            mealKitPayload.lockerNumber()
        );
    }
}
