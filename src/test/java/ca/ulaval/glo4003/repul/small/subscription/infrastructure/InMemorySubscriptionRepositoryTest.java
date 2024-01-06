package ca.ulaval.glo4003.repul.small.subscription.infrastructure;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.application.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriptionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class InMemorySubscriptionRepositoryTest {
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_VALID_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier ANOTHER_SUBSCRIPTION_VALID_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_VALID_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier AN_ORDER_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    @Mock
    private Subscription mockSubscription;
    @Mock
    private Subscription mockOtherSubscription;

    private InMemorySubscriptionRepository inMemorySubscriptionRepository;

    @BeforeEach
    public void createSubscriptionRepository() {
        inMemorySubscriptionRepository = new InMemorySubscriptionRepository();
    }

    @Test
    public void whenSavingSubscriptionAndGettingSubscriptionById_shouldReturnTheRightSubscription() {
        given(mockSubscription.getSubscriptionId()).willReturn(A_SUBSCRIPTION_VALID_ID);

        inMemorySubscriptionRepository.save(mockSubscription);
        Subscription subscriptionFound = inMemorySubscriptionRepository.getById(A_SUBSCRIPTION_VALID_ID);

        assertEquals(mockSubscription, subscriptionFound);
    }

    @Test
    public void givenNoSubscription_whenGettingSubscriptionById_shouldThrowSubscriptionNotFoundException() {
        assertThrows(SubscriptionNotFoundException.class, () -> inMemorySubscriptionRepository.getById(A_SUBSCRIPTION_VALID_ID));
    }

    @Test
    public void givenNoSubscriptionsForSubscriber_whenGettingSubscriptionsBySubscriberId_shouldReturnEmptyList() {
        List<Subscription> subscriptionsFound = inMemorySubscriptionRepository.getBySubscriberId(A_SUBSCRIBER_VALID_ID);

        assertTrue(subscriptionsFound.isEmpty());
    }

    @Test
    public void givenSubscriptionsForSubscriber_whenGettingSubscriptionsBySubscriberId_shouldReturnMatchingSubscriptions() {
        Subscription anotherSubscription = mock(Subscription.class);
        given(mockSubscription.getSubscriptionId()).willReturn(A_SUBSCRIPTION_VALID_ID);
        given(mockSubscription.getSubscriberId()).willReturn(A_SUBSCRIBER_VALID_ID);
        given(anotherSubscription.getSubscriptionId()).willReturn(ANOTHER_SUBSCRIPTION_VALID_ID);
        given(anotherSubscription.getSubscriberId()).willReturn(A_SUBSCRIBER_VALID_ID);
        inMemorySubscriptionRepository.save(mockSubscription);
        inMemorySubscriptionRepository.save(anotherSubscription);

        List<Subscription> subscriptionsFound = inMemorySubscriptionRepository.getBySubscriberId(A_SUBSCRIBER_VALID_ID);

        assertTrue(subscriptionsFound.contains(mockSubscription));
        assertTrue(subscriptionsFound.contains(anotherSubscription));
        assertEquals(2, subscriptionsFound.size());
    }

    @Test
    public void givenNoSubscriptionWithSpecificOrder_whenGettingSubscriptionByOrderId_shouldThrowOrderNotFoundException() {
        assertThrows(OrderNotFoundException.class, () -> inMemorySubscriptionRepository.getSubscriptionByOrderId(AN_ORDER_ID));
    }

    @Test
    public void givenSubscriptionWithSpecificOrder_whenGettingSubscriptionByOrderId_shouldReturnMatchingSubscription() {
        given(mockSubscription.getSubscriptionId()).willReturn(A_SUBSCRIPTION_VALID_ID);
        Order anOrder = mock(Order.class);
        given(anOrder.getOrderId()).willReturn(AN_ORDER_ID);
        given(mockSubscription.findCurrentOrder()).willReturn(Optional.of(anOrder));
        inMemorySubscriptionRepository.save(mockSubscription);

        Subscription subscriptionFound = inMemorySubscriptionRepository.getSubscriptionByOrderId(AN_ORDER_ID);

        assertEquals(mockSubscription, subscriptionFound);
    }

    @Test
    public void givenSubscriptions_whenGetAll_shouldReturnAllSubscriptions() {
        given(mockSubscription.getSubscriptionId()).willReturn(A_SUBSCRIPTION_VALID_ID);
        given(mockOtherSubscription.getSubscriptionId()).willReturn(ANOTHER_SUBSCRIPTION_VALID_ID);
        inMemorySubscriptionRepository.save(mockSubscription);
        inMemorySubscriptionRepository.save(mockOtherSubscription);

        List<Subscription> subscriptions = inMemorySubscriptionRepository.getAll();

        assertEquals(2, subscriptions.size());
    }
}
