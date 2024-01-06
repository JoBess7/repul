package ca.ulaval.glo4003.repul.medium.subscription;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriberServiceTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.parse("1969-04-20"));
    private static final Gender A_GENDER = Gender.OTHER;
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final UserCardNumber A_CARD_NUMBER = new UserCardNumber("123456789");

    private SubscriberService subscriberService;

    @BeforeEach
    public void createSubscriberService() {
        SubscriberRepository subscriberRepository = new InMemorySubscriberRepository();
        subscriberService = new SubscriberService(subscriberRepository, new SubscriberFactory(), new GuavaEventBus());
    }

    @Test
    public void whenCreatingSubscriber_shouldPersistSubscriber() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);

        ProfilePayload retrievedProfile = subscriberService.getSubscriberProfile(A_SUBSCRIBER_ID);
        assertEquals(AN_IDUL.value(), retrievedProfile.idul());
    }

    @Test
    public void whenAddingCard_shouldAddCardToSubscriber() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);

        subscriberService.addCard(A_SUBSCRIBER_ID, A_CARD_NUMBER);

        ProfilePayload retrievedProfile = subscriberService.getSubscriberProfile(A_SUBSCRIBER_ID);
        assertEquals(A_CARD_NUMBER.value(), retrievedProfile.cardNumber());
    }
}
