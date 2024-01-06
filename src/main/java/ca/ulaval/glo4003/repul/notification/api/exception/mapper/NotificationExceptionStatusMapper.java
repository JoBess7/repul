package ca.ulaval.glo4003.repul.notification.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.notification.application.exception.DeliveryPersonAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.application.exception.NotificationException;
import ca.ulaval.glo4003.repul.notification.application.exception.UserAccountNotFoundException;

import jakarta.ws.rs.core.Response;

public class NotificationExceptionStatusMapper {
    private static WeakHashMap<Class<? extends NotificationException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends NotificationException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(DeliveryPersonAccountNotFoundException.class, Response.Status.INTERNAL_SERVER_ERROR);
        exceptionMapper.put(UserAccountNotFoundException.class, Response.Status.INTERNAL_SERVER_ERROR);
    }
}
