package ca.ulaval.glo4003.repul.cooking.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitAlreadySelectedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotCookedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotForKitchenPickUpException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotInSelectionException;

public class Kitchen {
    private final KitchenLocationId kitchenLocationId;
    private final Map<MealKitUniqueIdentifier, MealKit> mealKits = new HashMap<>();
    private final MealKitFactory mealKitFactory;

    public Kitchen(MealKitFactory mealKitFactory) {
        this.mealKitFactory = mealKitFactory;
        kitchenLocationId = KitchenLocationId.DESJARDINS;
    }

    public KitchenLocationId getKitchenLocationId() {
        return kitchenLocationId;
    }

    public void createMealKitInPreparation(MealKitUniqueIdentifier mealKitId, SubscriberUniqueIdentifier subscriberId,
                                           MealKitType type, LocalDate deliveryDate, Optional<DeliveryLocationId> deliveryLocationId) {

        MealKit mealKit = mealKitFactory.createMealKit(mealKitId, subscriberId, type, deliveryDate, deliveryLocationId);
        mealKits.put(mealKitId, mealKit);
    }

    public List<MealKit> getMealKitsToCook() {
        return mealKits.values().stream().filter(MealKit::isToCookToday).toList();
    }

    public void select(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> selectedMealKitIds) {
        verifyMealKitsAreUnassigned(selectedMealKitIds);

        selectedMealKitIds.forEach(id -> {
            MealKit mealKit = getMealKit(id);
            mealKit.selectBy(cookId);
        });
    }

    public List<MealKitUniqueIdentifier> getSelection(CookUniqueIdentifier cookId) {
        return getSelectedMealKits(cookId).stream().map(MealKit::getMealKitId).toList();
    }

    public void cancelOneSelected(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        verifyMealKitsInCookSelection(cookId, List.of(mealKitId));
        getMealKit(mealKitId).unselect();
    }

    public void cancelAllSelected(CookUniqueIdentifier cookId) {
        unselectSelectedMealKits(cookId);
    }

    public MealKit confirmCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        verifyMealKitsInCookSelection(cookId, List.of(mealKitId));
        MealKit mealKit = getMealKit(mealKitId);
        mealKit.setCooked();
        return mealKit;
    }

    public List<MealKit> confirmCooked(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        verifyMealKitsInCookSelection(cookId, mealKitIds);

        List<MealKit> mealKits = mealKitIds.stream().map(this::getMealKit).toList();
        mealKits.forEach(MealKit::setCooked);

        return mealKits;
    }

    public void recallCooked(CookUniqueIdentifier cookId, MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = getMealKit(mealKitId);
        mealKit.recallMealKit(cookId);
    }

    public void removeMealKitsFromKitchen(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(mealKits::remove);
    }

    public MealKit pickupNonDeliverableMealKit(SubscriberUniqueIdentifier subscriberId, MealKitUniqueIdentifier mealKitId) {
        if (!mealKits.containsKey(mealKitId) || !mealKits.get(mealKitId).isForSubscriber(subscriberId)) {
            throw new MealKitNotFoundException(mealKitId);
        }

        MealKit mealKit = mealKits.get(mealKitId);
        if (!mealKit.isCooked()) {
            throw new MealKitNotCookedException();
        } else if (mealKit.isToBeDelivered()) {
            throw new MealKitNotForKitchenPickUpException(mealKitId);
        }

        mealKits.remove(mealKitId);
        return mealKit;
    }

    private void verifyMealKitsAreUnassigned(List<MealKitUniqueIdentifier> mealKitIds) {
        mealKitIds.forEach(this::verifyMealKitIsUnassigned);
    }

    private void verifyMealKitIsUnassigned(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = getMealKit(mealKitId);
        if (!mealKit.isUnselected()) {
            throw new MealKitAlreadySelectedException(mealKitId);
        }
    }

    private void verifyMealKitsInCookSelection(CookUniqueIdentifier cookId, List<MealKitUniqueIdentifier> mealKitIds) {
        List<MealKitUniqueIdentifier> cookSelection = getSelection(cookId);
        mealKitIds.forEach(mealKitId -> {
            if (!cookSelection.contains(mealKitId)) {
                throw new MealKitNotInSelectionException(mealKitId);
            }
        });
    }

    private void unselectSelectedMealKits(CookUniqueIdentifier cookId) {
        getSelectedMealKits(cookId).forEach(MealKit::unselect);
    }

    private MealKit getMealKit(MealKitUniqueIdentifier mealKitId) {
        MealKit mealKit = mealKits.get(mealKitId);
        if (mealKit == null) {
            throw new MealKitNotFoundException(mealKitId);
        }
        return mealKit;
    }

    private List<MealKit> getSelectedMealKits(CookUniqueIdentifier cookId) {
        return mealKits.values().stream().filter(mealKit -> mealKit.isSelectedBy(cookId)).toList();
    }
}
