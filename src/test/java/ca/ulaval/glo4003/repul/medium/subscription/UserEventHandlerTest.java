package ca.ulaval.glo4003.repul.medium.subscription;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.subscription.api.UserEventHandler;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriberRepository;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserEventHandlerTest {
    private static final UniqueIdentifier A_USER_ID = new UniqueIdentifierFactory<>(UniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.now().minusYears(18));
    private static final Gender A_GENDER = Gender.MAN;
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");

    private UserEventHandler userEventHandler;
    private RepULEventBus eventBus;
    private SubscriberService subscriberService;
    private SubscriberRepository subscriberRepository;

    @BeforeEach
    public void createUserEventHandler() {
        subscriberRepository = new InMemorySubscriberRepository();
        eventBus = new GuavaEventBus();
        subscriberService = new SubscriberService(subscriberRepository, new SubscriberFactory(), eventBus);
        userEventHandler = new UserEventHandler(subscriberService);
        eventBus.register(userEventHandler);
    }

    @Test
    public void whenHandlingUserCreatedEvent_shouldAddSubscriberToRepository() {
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(A_USER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);

        eventBus.publish(userCreatedEvent);

        SubscriberUniqueIdentifier subscriberId = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(A_USER_ID);
        assertDoesNotThrow(() -> subscriberRepository.getById(subscriberId));
    }
}
