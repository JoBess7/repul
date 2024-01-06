package ca.ulaval.glo4003.repul.cooking.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.cooking.domain.exception.CookingException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidQuantityUnitException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidQuantityValueException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidRecipeException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.KitchenNotFoundException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitAlreadySelectedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotCookedException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotForKitchenPickUpException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotFoundException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.MealKitNotInSelectionException;

import jakarta.ws.rs.core.Response;

public class CookingExceptionStatusMapper {
    private static WeakHashMap<Class<? extends CookingException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends CookingException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(KitchenNotFoundException.class, Response.Status.INTERNAL_SERVER_ERROR);

        exceptionMapper.put(InvalidQuantityUnitException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidQuantityValueException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidRecipeException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(MealKitAlreadySelectedException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(MealKitNotInSelectionException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(MealKitNotCookedException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(MealKitNotForKitchenPickUpException.class, Response.Status.BAD_REQUEST);

        exceptionMapper.put(MealKitNotFoundException.class, Response.Status.NOT_FOUND);
    }
}
