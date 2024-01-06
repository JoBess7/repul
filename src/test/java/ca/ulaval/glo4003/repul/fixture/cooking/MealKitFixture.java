package ca.ulaval.glo4003.repul.fixture.cooking;

import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.domain.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;

public class MealKitFixture {
    private MealKitUniqueIdentifier mealKitId;
    private SubscriberUniqueIdentifier subscriberId;
    private List<Recipe> recipes;
    private LocalDate deliveryDate;
    private boolean isToBeDelivered;

    public MealKitFixture() {
        this.mealKitId = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
        this.subscriberId = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
        this.recipes = List.of(new RecipeFixture().build());
        this.deliveryDate = LocalDate.of(2020, 1, 1);
        this.isToBeDelivered = true;
    }

    public MealKitFixture withMealKitId(MealKitUniqueIdentifier mealKitId) {
        this.mealKitId = mealKitId;
        return this;
    }

    public MealKitFixture withSubscriberId(SubscriberUniqueIdentifier subscriberId) {
        this.subscriberId = subscriberId;
        return this;
    }

    public MealKitFixture withRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        return this;
    }

    public MealKitFixture withDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
        return this;
    }

    public MealKitFixture withIsToBeDelivered(boolean isToBeDelivered) {
        this.isToBeDelivered = isToBeDelivered;
        return this;
    }

    public MealKit build() {
        return new MealKit(mealKitId, subscriberId, deliveryDate, recipes, isToBeDelivered);
    }
}
