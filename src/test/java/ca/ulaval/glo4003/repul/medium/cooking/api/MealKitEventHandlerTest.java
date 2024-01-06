package ca.ulaval.glo4003.repul.medium.cooking.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.cooking.api.MealKitEventHandler;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.cooking.domain.MealKitFactory;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;
import ca.ulaval.glo4003.repul.cooking.infrastructure.InMemoryKitchenPersister;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MealKitEventHandlerTest {
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier DEFAULT_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final CookUniqueIdentifier A_COOK_ID = new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generate();
    private static final LocalDate A_DELIVERY_DATE = LocalDate.now().plusDays(1);

    private MealKitEventHandler mealKitEventHandler;
    private RepULEventBus eventBus;
    private CookingService cookingService;
    private KitchenPersister kitchenPersister;

    @BeforeEach
    public void createMealKitEventHandler() {
        kitchenPersister = new InMemoryKitchenPersister();
        kitchenPersister.save(createKitchenWithAnUnselectedMealKit());
        eventBus = new GuavaEventBus();
        cookingService = new CookingService(kitchenPersister, eventBus);
        mealKitEventHandler = new MealKitEventHandler(cookingService);
        eventBus.register(mealKitEventHandler);
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldAddMealKitToKitchen() {
        MealKitConfirmedEvent mealKitConfirmedEvent =
            new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ID, A_MEAL_KIT_TYPE, Optional.of(A_DELIVERY_LOCATION_ID), A_DELIVERY_DATE);
        assertThrows(MealKitNotFoundException.class, () -> cookingService.select(A_COOK_ID, List.of(A_MEAL_KIT_ID)));

        eventBus.publish(mealKitConfirmedEvent);

        assertDoesNotThrow(() -> cookingService.select(A_COOK_ID, List.of(A_MEAL_KIT_ID)));
    }

    @Test
    public void whenHandlingPickUpCargoEvent_shouldRemoveMealKitFromKitchen() {
        PickedUpCargoEvent pickedUpCargoEvent = new PickedUpCargoEvent(List.of(DEFAULT_MEAL_KIT_ID));
        cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID));

        eventBus.publish(pickedUpCargoEvent);

        assertThrows(MealKitNotFoundException.class, () -> cookingService.select(A_COOK_ID, List.of(DEFAULT_MEAL_KIT_ID)));
    }

    private Kitchen createKitchenWithAnUnselectedMealKit() {
        Kitchen kitchen = new Kitchen(new MealKitFactory());
        kitchen.createMealKitInPreparation(DEFAULT_MEAL_KIT_ID, A_SUBSCRIBER_ID, A_MEAL_KIT_TYPE, A_DELIVERY_DATE, Optional.of(A_DELIVERY_LOCATION_ID));
        return kitchen;
    }
}
