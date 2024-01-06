package ca.ulaval.glo4003.repul.cooking.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotCookedException;

public class MealKit {
    private final MealKitUniqueIdentifier mealKitId;
    private final SubscriberUniqueIdentifier subscriberId;
    private final LocalDate dateOfReceipt;
    private final List<Recipe> recipes;
    private Optional<CookUniqueIdentifier> cookId = Optional.empty();
    private boolean isCooked;
    private final boolean isToBeDelivered;

    public MealKit(MealKitUniqueIdentifier mealKitId, SubscriberUniqueIdentifier subscriberId,
                   LocalDate dateOfReceipt, List<Recipe> recipes, boolean isToBeDelivered) {
        this.mealKitId = mealKitId;
        this.subscriberId = subscriberId;
        this.dateOfReceipt = dateOfReceipt;
        this.recipes = recipes;
        this.isCooked = false;
        this.isToBeDelivered = isToBeDelivered;
    }

    public MealKitUniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public LocalDate getDateOfReceipt() {
        return dateOfReceipt;
    }

    public void selectBy(CookUniqueIdentifier cookId) {
        this.cookId = Optional.of(cookId);
    }

    public void unselect() {
        cookId = Optional.empty();
    }

    public boolean isSelectedBy(CookUniqueIdentifier cookId) {
        return this.cookId.equals(Optional.of(cookId)) && !isCooked;
    }

    public boolean isForSubscriber(SubscriberUniqueIdentifier subscriberId) {
        return this.subscriberId.equals(subscriberId);
    }

    public boolean isUnselected() {
        return cookId.isEmpty();
    }

    public boolean isDeliveryTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return dateOfReceipt.isEqual(tomorrow);
    }

    public boolean isToCookToday() {
        return isUnselected() && !isCooked() && isDeliveryTomorrow();
    }

    public void setCooked() {
        this.isCooked = true;
    }

    public void recallMealKit(CookUniqueIdentifier cookId) {
        if (!isCooked()) {
            throw new MealKitNotCookedException();
        }
        this.isCooked = false;
        this.cookId = Optional.of(cookId);
    }

    public boolean isCooked() {
        return isCooked;
    }

    public boolean isToBeDelivered() {
        return isToBeDelivered;
    }
}
