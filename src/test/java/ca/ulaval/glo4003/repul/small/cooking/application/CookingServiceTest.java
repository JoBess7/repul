package ca.ulaval.glo4003.repul.small.cooking.application;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.cooking.domain.MealKit;
import ca.ulaval.glo4003.repul.fixture.cooking.MealKitFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CookingServiceTest {
    private static final MealKitUniqueIdentifier A_UNIQUE_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier ANOTHER_UNIQUE_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final CookUniqueIdentifier A_COOK_ID = new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generate();
    private static final List<MealKitUniqueIdentifier> MANY_MEAL_KIT_IDS = List.of(A_UNIQUE_MEAL_KIT_ID, ANOTHER_UNIQUE_MEAL_KIT_ID);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final MealKit MATCHING_MEAL_KIT = new MealKitFixture().withMealKitId(A_UNIQUE_MEAL_KIT_ID).build();
    private static final MealKit ANOTHER_MATCHING_MEAL_KIT = new MealKitFixture().withMealKitId(ANOTHER_UNIQUE_MEAL_KIT_ID).build();
    private static final List<MealKit> MATCHING_MEAL_KITS = List.of(MATCHING_MEAL_KIT, ANOTHER_MATCHING_MEAL_KIT);

    private CookingService cookingService;
    @Mock
    private KitchenPersister kitchenPersister;
    @Mock
    private Kitchen kitchen;
    @Mock
    private RepULEventBus eventBus;

    @BeforeEach
    public void createCookingService() {
        this.cookingService = new CookingService(kitchenPersister, eventBus);
        when(kitchenPersister.get()).thenReturn(kitchen);
    }

    @Test
    public void whenConfirmCooked_shouldPublishMealKitsCookedEvent() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);
        when(kitchen.confirmCooked(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID)).thenReturn(MATCHING_MEAL_KIT);
        ArgumentCaptor<MealKitsCookedEvent> eventCaptor = ArgumentCaptor.forClass(MealKitsCookedEvent.class);

        cookingService.confirmCooked(A_COOK_ID, A_UNIQUE_MEAL_KIT_ID);

        verify(eventBus).publish(eventCaptor.capture());
        MealKitsCookedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(A_KITCHEN_LOCATION_ID.toString(), publishedEvent.kitchenLocationId);
        assertEquals(List.of(A_UNIQUE_MEAL_KIT_ID), publishedEvent.mealKits.stream().map(MealKitDto::mealKitId).toList());
    }

    @Test
    public void whenConfirmCookedWithMultipleIds_shouldPublishMealKitsCookedEvent() {
        when(kitchen.getKitchenLocationId()).thenReturn(A_KITCHEN_LOCATION_ID);
        when(kitchen.confirmCooked(A_COOK_ID, MANY_MEAL_KIT_IDS)).thenReturn(MATCHING_MEAL_KITS);
        ArgumentCaptor<MealKitsCookedEvent> eventCaptor = ArgumentCaptor.forClass(MealKitsCookedEvent.class);

        cookingService.confirmCooked(A_COOK_ID, MANY_MEAL_KIT_IDS);

        verify(eventBus).publish(eventCaptor.capture());
        MealKitsCookedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(A_KITCHEN_LOCATION_ID.toString(), publishedEvent.kitchenLocationId);
        assertEquals(MANY_MEAL_KIT_IDS, publishedEvent.mealKits.stream().map(MealKitDto::mealKitId).toList());
    }
}
