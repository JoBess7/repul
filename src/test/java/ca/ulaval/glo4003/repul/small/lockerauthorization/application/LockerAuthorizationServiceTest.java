package ca.ulaval.glo4003.repul.small.lockerauthorization.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LockerAuthorizationServiceTest {
    private static final LockerId A_LOCKER_CONTEXT_LOCKER_ID = new LockerId("VACHON 1");
    private static final UserCardNumber A_USER_CARD_NUMBER = new UserCardNumber("123123123");
    private static final OpenLockerQuery OPEN_LOCKER_QUERY = new OpenLockerQuery(A_USER_CARD_NUMBER, A_LOCKER_CONTEXT_LOCKER_ID);

    @Mock
    private LockerAuthorizationSystem lockerAuthorizationSystem;
    @Mock
    private LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository;
    @Mock
    private RepULEventBus eventBus;

    private LockerAuthorizationService lockerAuthorizationService;

    @BeforeEach
    public void createLockerAuthorizationService() {
        lockerAuthorizationService = new LockerAuthorizationService(eventBus, lockerAuthorizationSystemRepository);

        when(lockerAuthorizationSystemRepository.get()).thenReturn(lockerAuthorizationSystem);
    }

    @Test
    public void whenOpenLocker_shouldPublishMealKitPickedUpByUserEvent() {
        lockerAuthorizationService.openLocker(OPEN_LOCKER_QUERY);

        verify(eventBus).publish(any(MealKitPickedUpByUserEvent.class));
    }
}
