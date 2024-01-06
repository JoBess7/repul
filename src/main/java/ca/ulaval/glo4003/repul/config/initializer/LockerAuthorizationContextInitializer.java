package ca.ulaval.glo4003.repul.config.initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationEventHandler;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.infrastructure.InMemoryLockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.middleware.ApiKeyGuard;

public class LockerAuthorizationContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplicationContext.class);

    private LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository = new InMemoryLockerAuthorizationSystemRepository();
    private final List<Map.Entry<SubscriberUniqueIdentifier, MealKitUniqueIdentifier>> orders = new ArrayList<>();

    public LockerAuthorizationContextInitializer withEmptyLockerAuthorizationSystemRepository(LockerAuthorizationSystemRepository repository) {
        lockerAuthorizationSystemRepository = repository;
        return this;
    }

    public LockerAuthorizationContextInitializer withOrders(List<Map.Entry<SubscriberUniqueIdentifier, MealKitUniqueIdentifier>> orders) {
        this.orders.addAll(orders);
        return this;
    }

    public LockerAuthorizationService createLockerAuthorizationService(RepULEventBus eventBus) {
        LOGGER.info("Creating locker authorization service");
        initializeLockerAuthorization(lockerAuthorizationSystemRepository);
        return new LockerAuthorizationService(eventBus, lockerAuthorizationSystemRepository);
    }

    public void createLockerAuthorizationEventHandler(LockerAuthorizationService lockerAuthorizationService, RepULEventBus eventBus) {
        LockerAuthorizationEventHandler lockerAuthorizationEventHandler = new LockerAuthorizationEventHandler(lockerAuthorizationService);
        eventBus.register(lockerAuthorizationEventHandler);
    }

    private void initializeLockerAuthorization(LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository) {
        LockerAuthorizationSystem lockerAuthorizationSystem = new LockerAuthorizationSystem();
        orders.forEach(order -> {
            SubscriberUniqueIdentifier accountId = order.getKey();
            MealKitUniqueIdentifier mealKitId = order.getValue();
            lockerAuthorizationSystem.createOrder(accountId, mealKitId);
        });
        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public ApiKeyGuard createApiKeyGuard() {
        return new ApiKeyGuard();
    }
}
