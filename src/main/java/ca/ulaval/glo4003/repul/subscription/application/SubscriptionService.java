package ca.ulaval.glo4003.repul.subscription.application;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.application.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

import com.google.common.eventbus.Subscribe;

public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionFactory subscriptionFactory;
    private final RepULEventBus eventBus;
    private final PaymentService paymentService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, SubscriptionFactory subscriptionFactory,
                               PaymentService paymentService, RepULEventBus eventBus) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionFactory = subscriptionFactory;
        this.eventBus = eventBus;
        this.paymentService = paymentService;
    }

    public SubscriptionUniqueIdentifier createSubscription(SubscriberUniqueIdentifier subscriberId, SubscriptionQuery subscriptionQuery) {
        Subscription subscription = subscriptionFactory.createSubscription(subscriberId, subscriptionQuery.deliveryLocationId(), subscriptionQuery.dayOfWeek(),
            subscriptionQuery.mealKitType());

        subscriptionRepository.save(subscription);

        return subscription.getSubscriptionId();
    }

    @Subscribe
    public void handleMealKitPickedUpByUserEvent(MealKitPickedUpByUserEvent event) {
        Subscription subscription =
            subscriptionRepository.getSubscriptionByOrderId(event.mealKitId);
        subscription.markOrderAsPickedUp(event.mealKitId);
        subscriptionRepository.save(subscription);
    }

    @Subscribe
    public void handleMealKitsCookedEvent(MealKitsCookedEvent event) {
        for (MealKitDto mealKit : event.mealKits) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(mealKit.mealKitId());
            subscription.markCurrentOrderAsToDeliver();
            subscriptionRepository.save(subscription);
        }
    }

    @Subscribe
    public void handleRecallCookedMealKitEvent(RecallCookedMealKitEvent recallCookedMealKitEvent) {
        Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(recallCookedMealKitEvent.mealKitId);
        subscription.markCurrentOrderAsToCook();
        subscriptionRepository.save(subscription);
    }

    @Subscribe
    public void handlePickedUpCargoEvent(PickedUpCargoEvent event) {
        for (MealKitUniqueIdentifier orderId : event.mealKitIds) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(orderId);
            subscription.markCurrentOrderAsInDelivery();
            subscriptionRepository.save(subscription);
        }
    }

    @Subscribe
    public void handleCanceledCargoEvent(CanceledCargoEvent event) {
        for (MealKitUniqueIdentifier orderId : event.mealKitIds) {
            Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(orderId);
            subscription.markCurrentOrderAsToDeliver();
            subscriptionRepository.save(subscription);
        }
    }

    @Subscribe
    public void handleConfirmedDeliveryEvent(ConfirmedDeliveryEvent event) {
        Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(event.mealKitId);
        subscription.markCurrentOrderAsToPickUp();
        subscriptionRepository.save(subscription);
    }

    @Subscribe
    public void handleRecalledDeliveryEvent(RecalledDeliveryEvent event) {
        Subscription subscription = subscriptionRepository.getSubscriptionByOrderId(event.mealKitId);
        subscription.markCurrentOrderAsInDelivery();
        subscriptionRepository.save(subscription);
    }

    public SubscriptionsPayload getSubscriptions(SubscriberUniqueIdentifier subscriberId) {
        List<Subscription> subscriptions = subscriptionRepository.getBySubscriberId(subscriberId);

        List<SubscriptionPayload> subscriptionPayloads = subscriptions.stream().map(SubscriptionPayload::from).toList();

        return new SubscriptionsPayload(subscriptionPayloads);
    }

    public SubscriptionPayload getSubscriptionById(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);
        return SubscriptionPayload.from(subscription);
    }

    public void confirmNextMealKitForSubscription(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);

        subscription.confirmNextMealKit();

        subscriptionRepository.save(subscription);
    }

    public void declineNextMealKitForSubscription(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = getSubscription(subscriberId, subscriptionId);

        subscription.declineNextMealKit();

        subscriptionRepository.save(subscription);
    }

    private Subscription getSubscription(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId) {
        Subscription subscription = subscriptionRepository.getById(subscriptionId);

        if (!subscription.getSubscriberId().equals(subscriberId)) {
            throw new SubscriptionNotFoundException();
        }
        return subscription;
    }

    public OrdersPayload getCurrentOrders(SubscriberUniqueIdentifier subscriberId) {
        List<Subscription> subscriptions = subscriptionRepository.getBySubscriberId(subscriberId);

        List<Order> currentOrders = subscriptions.stream().map(Subscription::findCurrentOrder).filter(Optional::isPresent).map(Optional::get).toList();

        return OrdersPayload.from(currentOrders);
    }

    public void processConfirmationForTheDay() {
        List<Subscription> subscriptions = subscriptionRepository.getAll();
        for (Subscription subscription : subscriptions) {
            processConfirmedOrders(subscription);
            processPendingOrders(subscription);
        }
    }

    private void processConfirmedOrders(Subscription subscription) {
        List<Order> ordersToProcess = subscription.getOrdersReadyToCook();
        for (Order order : ordersToProcess) {
            paymentService.pay(subscription.getSubscriberId(), subscription.getMealKitType(), order.getDeliveryDate());

            sendMealKitConfirmedEvent(subscription, order);

            order.markAsToCook();
        }
    }

    private void processPendingOrders(Subscription subscription) {
        List<Order> ordersToProcess = subscription.getOverdueOrders();
        ordersToProcess.forEach(Order::markAsDeclined);
    }

    private void sendMealKitConfirmedEvent(Subscription subscription, Order confirmedOrder) {
        MealKitConfirmedEvent event = new MealKitConfirmedEvent(
            confirmedOrder.getOrderId(),
            subscription.getSubscriptionId(),
            subscription.getSubscriberId(),
            subscription.getMealKitType(),
            Optional.of(subscription.getDeliveryLocationId()),
            confirmedOrder.getDeliveryDate()
        );

        eventBus.publish(event);
    }
}
