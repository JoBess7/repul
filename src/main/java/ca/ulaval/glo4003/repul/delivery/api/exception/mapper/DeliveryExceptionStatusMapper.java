package ca.ulaval.glo4003.repul.delivery.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.delivery.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliverySystemNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.CargoAlreadyPickedUpException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.DeliveryException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidCargoIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidDeliveryPersonIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidLockerIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotDeliveredException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotInCargoException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotInDeliveryException;

import jakarta.ws.rs.core.Response;

public class DeliveryExceptionStatusMapper {
    private static WeakHashMap<Class<? extends DeliveryException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends DeliveryException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(DeliverySystemNotFoundException.class, Response.Status.INTERNAL_SERVER_ERROR);
        exceptionMapper.put(InvalidMealKitIdException.class, Response.Status.INTERNAL_SERVER_ERROR);

        exceptionMapper.put(DeliveryPersonNotFoundException.class, Response.Status.NOT_FOUND);

        exceptionMapper.put(InvalidLockerIdException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidDeliveryPersonIdException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidCargoIdException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(MealKitNotDeliveredException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(MealKitNotInDeliveryException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(CargoAlreadyPickedUpException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(LockerNotAssignedException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(MealKitNotInCargoException.class, Response.Status.BAD_REQUEST);
    }
}
