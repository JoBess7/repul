package ca.ulaval.glo4003.repul.cooking.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.cooking.domain.MealKit;

public class CookingService {
    private final KitchenPersister kitchenPersister;

    private final RepULEventBus eventBus;

    public CookingService(KitchenPersister kitchenPersister, RepULEventBus eventBus) {
        this.kitchenPersister = kitchenPersister;
        this.eventBus = eventBus;
    }

    public void createMealKitInPreparation(MealKitUniqueIdentifier mealKitId, SubscriberUniqueIdentifier subscriberId,
                                           MealKitType mealKitType, LocalDate deliveryDate, Optional<DeliveryLocationId> deliveryLocationId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.createMealKitInPreparation(mealKitId, subscriberId, mealKitType, deliveryDate, deliveryLocationId);

        kitchenPersister.save(kitchen);
    }

    public void giveMealKitToDelivery(List<MealKitUniqueIdentifier> mealKitIds) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.removeMealKitsFromKitchen(mealKitIds);

        kitchenPersister.save(kitchen);
    }

    public MealKitsPayload getMealKitsToCook() {
        Kitchen kitchen = kitchenPersister.get();

        List<MealKitPayload> mealKitPayloads = kitchen.getMealKitsToCook().stream().map(
            mealKit -> new MealKitPayload(mealKit.getMealKitId(), mealKit.getDateOfReceipt(), mealKit.getRecipes())
        ).toList();
        return new MealKitsPayload(mealKitPayloads);
    }

    public void select(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> selectedMealKitIds) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.select(cookId, selectedMealKitIds);

        kitchenPersister.save(kitchen);
    }

    public List<MealKitUniqueIdentifier> getSelection(CookUniqueIdentifier cookId) {
        Kitchen kitchen = kitchenPersister.get();

        return kitchen.getSelection(cookId);
    }

    public void cancelOneSelected(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.cancelOneSelected(cookId, mealKitId);

        kitchenPersister.save(kitchen);
    }

    public void cancelAllSelected(CookUniqueIdentifier cookId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.cancelAllSelected(cookId);

        kitchenPersister.save(kitchen);
    }

    public void confirmCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        MealKit mealKit = kitchen.confirmCooked(cookId, mealKitId);

        kitchenPersister.save(kitchen);

        sendMealKitsCookedEvent(List.of(mealKit), kitchen);
    }

    public void confirmCooked(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        Kitchen kitchen = kitchenPersister.get();

        List<MealKit> mealKits = kitchen.confirmCooked(cookId, mealKitIds);

        kitchenPersister.save(kitchen);

        sendMealKitsCookedEvent(mealKits, kitchen);
    }

    public void recallCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.recallCooked(cookId, mealKitId);

        kitchenPersister.save(kitchen);

        eventBus.publish(new RecallCookedMealKitEvent(mealKitId));
    }

    public void pickupNonDeliverableMealKit(SubscriberUniqueIdentifier subscriberId, MealKitUniqueIdentifier mealKitId) {
        Kitchen kitchen = kitchenPersister.get();

        kitchen.pickupNonDeliverableMealKit(subscriberId, mealKitId);

        kitchenPersister.save(kitchen);
    }

    private void sendMealKitsCookedEvent(List<MealKit> mealKits, Kitchen kitchen) {
        List<MealKitDto> mealKitDtos = mealKits.stream().map(
            mealKit -> new MealKitDto(mealKit.getMealKitId(), mealKit.isToBeDelivered())
        ).toList();
        eventBus.publish(new MealKitsCookedEvent(kitchen.getKitchenLocationId().toString(), mealKitDtos));
    }
}
