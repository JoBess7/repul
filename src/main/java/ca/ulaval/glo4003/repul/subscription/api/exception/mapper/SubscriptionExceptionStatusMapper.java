package ca.ulaval.glo4003.repul.subscription.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.subscription.application.exception.CardNumberAlreadyInUseException;
import ca.ulaval.glo4003.repul.subscription.application.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidBirthdateException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidGenderException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidNameException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidSemesterCodeException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoNextOrderInSubscriptionException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoOrdersInDesiredPeriodException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeConfirmedException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeDeclinedException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SemesterNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SubscriberNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SubscriptionException;

import jakarta.ws.rs.core.Response;

public class SubscriptionExceptionStatusMapper {
    private static WeakHashMap<Class<? extends SubscriptionException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends SubscriptionException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(OrderNotFoundException.class, Response.Status.INTERNAL_SERVER_ERROR);

        exceptionMapper.put(SubscriptionNotFoundException.class, Response.Status.NOT_FOUND);
        exceptionMapper.put(SubscriberNotFoundException.class, Response.Status.NOT_FOUND);
        exceptionMapper.put(SemesterNotFoundException.class, Response.Status.NOT_FOUND);

        exceptionMapper.put(CardNumberAlreadyInUseException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidSemesterCodeException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(NoNextOrderInSubscriptionException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(NoOrdersInDesiredPeriodException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(OrderCannotBeDeclinedException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(OrderCannotBeConfirmedException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidBirthdateException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidGenderException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidNameException.class, Response.Status.BAD_REQUEST);
    }
}
