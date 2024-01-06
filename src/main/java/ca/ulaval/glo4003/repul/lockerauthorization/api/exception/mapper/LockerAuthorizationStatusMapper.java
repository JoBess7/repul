package ca.ulaval.glo4003.repul.lockerauthorization.api.exception.mapper;

import java.util.Map;
import java.util.WeakHashMap;

import ca.ulaval.glo4003.repul.lockerauthorization.application.exception.LockerAuthorizationSystemNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.InvalidLockerIdException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.LockerAuthorizationException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.NoCardLinkedToUserException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.UserCardNotAuthorizedException;

import jakarta.ws.rs.core.Response;

public class LockerAuthorizationStatusMapper {
    private static WeakHashMap<Class<? extends LockerAuthorizationException>, Response.Status> exceptionMapper;

    public static Map<Class<? extends LockerAuthorizationException>, Response.Status> getAll() {
        if (exceptionMapper == null) {
            createExceptionMapper();
        }
        return exceptionMapper;
    }

    private static void createExceptionMapper() {
        exceptionMapper = new WeakHashMap<>();

        exceptionMapper.put(OrderNotFoundException.class, Response.Status.INTERNAL_SERVER_ERROR);
        exceptionMapper.put(LockerAuthorizationSystemNotFoundException.class, Response.Status.INTERNAL_SERVER_ERROR);

        exceptionMapper.put(LockerNotAssignedException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(NoCardLinkedToUserException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(UserCardNotAuthorizedException.class, Response.Status.BAD_REQUEST);
        exceptionMapper.put(InvalidLockerIdException.class, Response.Status.BAD_REQUEST);
    }
}
