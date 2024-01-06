package ca.ulaval.glo4003.repul.small.subscription.infrastructure;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SubscriberNotFoundException;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemorySubscriberRepositoryTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_VALID_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final UserCardNumber AN_UNASSIGNED_CARD_NUMBER = new UserCardNumber("987654321");
    private static final UserCardNumber A_CARD_NUMBER = new UserCardNumber("123456789");

    private InMemorySubscriberRepository inMemorySubscriberRepository;
    @Mock
    private Subscriber subscriber;

    @BeforeEach
    public void createSubscriberRepository() {
        inMemorySubscriberRepository = new InMemorySubscriberRepository();
    }

    @Test
    public void whenSavingSubscriberAndGettingSubscriberById_shouldReturnTheRightSubscriber() {
        given(subscriber.getSubscriberId()).willReturn(A_SUBSCRIBER_VALID_ID);

        inMemorySubscriberRepository.save(subscriber);
        Subscriber subscriberFound = inMemorySubscriberRepository.getById(A_SUBSCRIBER_VALID_ID);

        assertEquals(subscriber, subscriberFound);
    }

    @Test
    public void givenNoSubscriber_whenGettingSubscriberById_shouldThrowSubscriberNotFoundException() {
        assertThrows(SubscriberNotFoundException.class, () -> inMemorySubscriberRepository.getById(A_SUBSCRIBER_VALID_ID));
    }

    @Test
    public void givenCardNumberNotAssociatedToSubscriber_whenCheckingIfCardNumberExists_shouldReturnFalse() {
        assertFalse(inMemorySubscriberRepository.cardNumberExists(AN_UNASSIGNED_CARD_NUMBER));
    }

    @Test
    public void givenCardNumberAssociatedToSubscriber_whenCheckingIfCardNumberExists_shouldReturnTrue() {
        given(subscriber.getCardNumber()).willReturn(Optional.of(A_CARD_NUMBER));
        inMemorySubscriberRepository.save(subscriber);

        boolean cardNumberExists = inMemorySubscriberRepository.cardNumberExists(A_CARD_NUMBER);

        assertTrue(cardNumberExists);
    }
}
