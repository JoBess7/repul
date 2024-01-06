package ca.ulaval.glo4003.repul.config.context;

import java.util.List;
import java.util.Map;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.api.exception.mapper.CatchallExceptionMapper;
import ca.ulaval.glo4003.repul.commons.api.exception.mapper.ConstraintViolationExceptionMapper;
import ca.ulaval.glo4003.repul.commons.api.exception.mapper.NotFoundExceptionMapper;
import ca.ulaval.glo4003.repul.commons.api.exception.mapper.RepULExceptionMapper;
import ca.ulaval.glo4003.repul.commons.api.jobs.RepULJob;
import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.config.env.EnvParserFactory;
import ca.ulaval.glo4003.repul.config.initializer.CookingContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.DeliveryContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.JobInitializer;
import ca.ulaval.glo4003.repul.config.initializer.LockerAuthorizationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.NotificationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.SubscriptionContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.UserContextInitializer;
import ca.ulaval.glo4003.repul.cooking.api.MealKitResource;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.delivery.api.CargoResource;
import ca.ulaval.glo4003.repul.delivery.api.LocationResource;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.health.api.HealthResource;
import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationResource;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.middleware.ApiKeyGuard;
import ca.ulaval.glo4003.repul.notification.infrastructure.EmailNotificationSender;
import ca.ulaval.glo4003.repul.subscription.api.AccountResource;
import ca.ulaval.glo4003.repul.subscription.api.SubscriptionResource;
import ca.ulaval.glo4003.repul.subscription.api.jobs.ProcessConfirmationForTheDayJob;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.infrastructure.LogPaymentService;
import ca.ulaval.glo4003.repul.user.api.UserResource;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.middleware.AuthGuard;

public class DevApplicationContext implements ApplicationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(DevApplicationContext.class);
    private static final EnvParser ENV_PARSER = EnvParserFactory.getEnvParser(".env");
    private static final int PORT = 8080;
    private static final CookUniqueIdentifier COOK_ID = new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generate();
    private static final RegistrationQuery COOK_REGISTRATION_QUERY =
        RegistrationQuery.from("paul@ulaval.ca", "paul123", "PAUL123", "Paul", "1990-01-01", "MAN");
    private static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID = new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final String DELIVERY_PERSON_EMAIL =
        ENV_PARSER.readVariable("DELIVERY_PERSON_EMAIL").isBlank() ? "roger@ulaval.ca" : ENV_PARSER.readVariable("DELIVERY_PERSON_EMAIL");
    private static final RegistrationQuery DELIVERY_PERSON_REGISTRATION_EMAIL =
        RegistrationQuery.from(DELIVERY_PERSON_EMAIL, "roger123", "ROGER456", "Roger", "1973-04-24", "MAN");

    @Override
    public ResourceConfig initializeResourceConfig() {
        RepULEventBus eventBus = new GuavaEventBus();

        LOGGER.info("Creating Health resource");
        HealthResource healthResource = new HealthResource();

        NotificationContextInitializer notificationContextInitializer =
            new NotificationContextInitializer().withNotificationSender(new EmailNotificationSender())
                .withDeliveryAccounts(List.of(Map.of(DELIVERY_PERSON_ID, DELIVERY_PERSON_EMAIL)));
        notificationContextInitializer.createNotificationService(eventBus);

        PaymentService paymentService = new LogPaymentService();

        UserContextInitializer userContextInitializer = new UserContextInitializer(eventBus).withCooks(List.of(Map.of(COOK_ID, COOK_REGISTRATION_QUERY)))
            .withShippers(List.of(Map.of(DELIVERY_PERSON_ID, DELIVERY_PERSON_REGISTRATION_EMAIL)));

        UserResource userResource = new UserResource(userContextInitializer.createService());
        AuthGuard authGuard = userContextInitializer.createAuthGuard();

        CookingContextInitializer cookingContextInitializer = new CookingContextInitializer();
        CookingService cookingService = cookingContextInitializer.createCookingService(eventBus);
        MealKitResource mealKitResource = new MealKitResource(cookingService);
        cookingContextInitializer.createMealKitEventHandler(cookingService, eventBus);

        SubscriptionContextInitializer subscriptionContextInitializer = new SubscriptionContextInitializer();
        SubscriberService subscriberService = subscriptionContextInitializer.createSubscriberService(eventBus);
        SubscriptionService subscriptionService = subscriptionContextInitializer.createSubscriptionService(eventBus, paymentService);
        AccountResource accountResource = new AccountResource(subscriberService);
        SubscriptionResource subscriptionResource = new SubscriptionResource(subscriptionService);
        RepULJob processConfirmationForTheDayJob = new ProcessConfirmationForTheDayJob(subscriptionService);
        subscriptionContextInitializer.createUserEventHandler(subscriberService, eventBus);

        DeliveryContextInitializer deliveryContextInitializer = new DeliveryContextInitializer().withDeliveryPeople(List.of(DELIVERY_PERSON_ID));
        DeliveryService deliveryService = deliveryContextInitializer.createDeliveryService(eventBus);
        CargoResource cargoResource = new CargoResource(deliveryService);
        LocationResource locationResource = new LocationResource(deliveryContextInitializer.createLocationsCatalogService());
        deliveryContextInitializer.createDeliveryEventHandler(deliveryService, eventBus);

        LockerAuthorizationContextInitializer lockerAuthorizationContextInitializer = new LockerAuthorizationContextInitializer();
        LockerAuthorizationService lockerAuthorizationService =
            lockerAuthorizationContextInitializer.createLockerAuthorizationService(eventBus);
        LockerAuthorizationResource lockerAuthorizationResource =
            new LockerAuthorizationResource(lockerAuthorizationService);
        lockerAuthorizationContextInitializer.createLockerAuthorizationEventHandler(lockerAuthorizationService, eventBus);
        ApiKeyGuard apiKeyGuard = lockerAuthorizationContextInitializer.createApiKeyGuard();

        JobInitializer jobInitializer = new JobInitializer().withJob(processConfirmationForTheDayJob);
        jobInitializer.launchJobs();

        // Setup resource config
        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
                bind(userResource).to(UserResource.class);
                bind(accountResource).to(AccountResource.class);
                bind(mealKitResource).to(MealKitResource.class);
                bind(cargoResource).to(CargoResource.class);
                bind(locationResource).to(LocationResource.class);
                bind(subscriptionResource).to(SubscriptionResource.class);
                bind(lockerAuthorizationResource).to(LockerAuthorizationResource.class);
            }
        };

        return new ResourceConfig().packages("ca.ulaval.glo4003.repul").property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true).register(binder)
            .register(authGuard).register(apiKeyGuard).register(new CatchallExceptionMapper()).register(new NotFoundExceptionMapper())
            .register(new RepULExceptionMapper()).register(new ConstraintViolationExceptionMapper());
    }

    @Override
    public String getURI() {
        return String.format("http://localhost:%s/api/", PORT);
    }
}
