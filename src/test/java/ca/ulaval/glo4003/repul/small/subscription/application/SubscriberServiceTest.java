package ca.ulaval.glo4003.repul.small.subscription.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriberFixture;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.event.UserCardAddedEvent;
import ca.ulaval.glo4003.repul.subscription.application.exception.CardNumberAlreadyInUseException;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriberServiceTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final UserCardNumber AN_UNASSIGNED_CARD_NUMBER = new UserCardNumber("123456789");
    private static final UserCardNumber AN_ASSIGNED_CARD_NUMBER = new UserCardNumber("987654321");

    private SubscriberService subscriberService;
    @Mock
    private SubscriberRepository subscriberRepository;
    @Mock
    private SubscriberFactory subscriberFactory;
    @Mock
    private RepULEventBus eventBus;

    @BeforeEach
    public void createSubscriberService() {
        subscriberService = new SubscriberService(subscriberRepository, subscriberFactory, eventBus);
    }

    @Test
    public void whenGettingSubscriberProfile_shouldReturnSubscriberProfilePayload() {
        Subscriber subscriber = new SubscriberFixture().withSubscriberId(A_SUBSCRIBER_ID).build();
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(subscriber);
        ProfilePayload expectedProfilePayload = ProfilePayload.from(subscriber.getProfile());

        ProfilePayload retrievedProfile = subscriberService.getSubscriberProfile(A_SUBSCRIBER_ID);

        assertEquals(expectedProfilePayload, retrievedProfile);
    }

    @Test
    public void givenAssignedCardNumber_whenAddingCard_shouldThrowCardNumberAlreadyInUseException() {
        Subscriber subscriber = new SubscriberFixture().withSubscriberId(A_SUBSCRIBER_ID).withCardNumber(AN_UNASSIGNED_CARD_NUMBER).build();
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(subscriber);
        given(subscriberRepository.cardNumberExists(AN_ASSIGNED_CARD_NUMBER)).willReturn(true);

        assertThrows(CardNumberAlreadyInUseException.class, () -> subscriberService.addCard(A_SUBSCRIBER_ID, AN_ASSIGNED_CARD_NUMBER));
    }

    @Test
    public void givenUnassignedCardNumber_whenAddingCard_shouldPublishUserCardAddedEvent() {
        Subscriber subscriber = new SubscriberFixture().withSubscriberId(A_SUBSCRIBER_ID).build();
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(subscriber);
        given(subscriberRepository.cardNumberExists(AN_UNASSIGNED_CARD_NUMBER)).willReturn(false);

        subscriberService.addCard(A_SUBSCRIBER_ID, AN_UNASSIGNED_CARD_NUMBER);

        verify(eventBus).publish(any(UserCardAddedEvent.class));
    }
}
