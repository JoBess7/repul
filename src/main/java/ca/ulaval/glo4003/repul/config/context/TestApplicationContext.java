package ca.ulaval.glo4003.repul.config.context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
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
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.health.api.HealthResource;
import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationResource;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.middleware.ApiKeyGuard;
import ca.ulaval.glo4003.repul.subscription.api.AccountResource;
import ca.ulaval.glo4003.repul.subscription.api.SubscriptionResource;
import ca.ulaval.glo4003.repul.subscription.api.jobs.ProcessConfirmationForTheDayJob;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.domain.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.infrastructure.LogPaymentService;
import ca.ulaval.glo4003.repul.user.api.UserResource;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.middleware.AuthGuard;

public class TestApplicationContext implements ApplicationContext {
    public static final SubscriberUniqueIdentifier CLIENT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    public static final String CLIENT_EMAIL = "alexandra@ulaval.ca";
    public static final String CLIENT_PASSWORD = "alexandra123";
    public static final CookUniqueIdentifier COOK_ID = new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generate();
    public static final String COOK_EMAIL = "paul@ulaval.ca";
    public static final String COOK_PASSWORD = "paul123";
    public static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID = new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    public static final DeliveryPersonUniqueIdentifier SECOND_DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    public static final String DELIVERY_PERSON_EMAIL = "roger@ulaval.ca";
    public static final String DELIVERY_PERSON_PASSWORD = "roger123";
    public static final String SECOND_DELIVERY_PERSON_EMAIL = "john@ulaval.ca";
    public static final String SECOND_DELIVERY_PERSON_PASSWORD = "john123";
    public static final SubscriptionUniqueIdentifier FIRST_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier SECOND_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier THIRD_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier FOURTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier FIFTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier SIXTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier SEVENTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier EIGHTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier NINTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier TENTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier SECOND_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier THIRD_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier FOURTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier FIFTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier SIXTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier SEVENTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier EIGHTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier NINTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier TENTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    public static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, "Entrée Vachon #1", 30);
    private static final Order FIRST_MEAL_KIT_ORDER = new Order(FIRST_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order SECOND_MEAL_KIT_ORDER = new Order(SECOND_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order THIRD_MEAL_KIT_ORDER = new Order(THIRD_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order FOURTH_MEAL_KIT_ORDER = new Order(FOURTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order FIFTH_MEAL_KIT_ORDER = new Order(FIFTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order SIXTH_MEAL_KIT_ORDER = new Order(SIXTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order SEVENTH_MEAL_KIT_ORDER = new Order(SEVENTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order EIGHTH_MEAL_KIT_ORDER = new Order(EIGHTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order NINTH_MEAL_KIT_ORDER = new Order(NINTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order TENTH_MEAL_KIT_ORDER = new Order(TENTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_DELIVER);
    private static final Frequency A_WEEKLY_FREQUENCY = new Frequency(LocalDate.now().getDayOfWeek());
    private static final Semester A_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now(), LocalDate.now().plusWeeks(10));
    private static final RegistrationQuery CLIENT_REGISTRATION_QUERY =
        RegistrationQuery.from(CLIENT_EMAIL, CLIENT_PASSWORD, "ALEXA123", "Alexandra", "1999-01-01", "WOMAN");
    private static final Subscriber SUBSCRIBER =
        new Subscriber(CLIENT_ID, new IDUL(CLIENT_REGISTRATION_QUERY.idul().value()), new Name(CLIENT_REGISTRATION_QUERY.name().value()),
            new Birthdate(CLIENT_REGISTRATION_QUERY.birthdate().value()), Gender.from(CLIENT_REGISTRATION_QUERY.gender().name()),
            CLIENT_REGISTRATION_QUERY.email());
    private static final RegistrationQuery COOK_REGISTRATION_QUERY = RegistrationQuery.from(COOK_EMAIL, COOK_PASSWORD, "PAUL123", "Paul", "1990-01-01", "MAN");
    private static final RegistrationQuery DELIVERY_PERSON_REGISTRATION_QUERY =
        RegistrationQuery.from(DELIVERY_PERSON_EMAIL, DELIVERY_PERSON_PASSWORD, "ROGER456", "Roger", "1973-04-24", "MAN");
    private static final RegistrationQuery SECOND_DELIVERY_PERSON_REGISTRATION_QUERY =
        RegistrationQuery.from(SECOND_DELIVERY_PERSON_EMAIL, SECOND_DELIVERY_PERSON_PASSWORD, "JOHN456", "John", "1973-05-24", "MAN");
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplicationContext.class);
    private static final int PORT = 8081;

    @Override
    public ResourceConfig initializeResourceConfig() {
        EnvParser.setFilename(".envExample");
        RepULEventBus eventBus = new GuavaEventBus();

        PaymentService paymentService = new LogPaymentService();

        LOGGER.info("Creating Health resource");
        HealthResource healthResource = new HealthResource();

        NotificationContextInitializer notificationContextInitializer =
            new NotificationContextInitializer().withDeliveryAccounts(List.of(Map.of(DELIVERY_PERSON_ID, DELIVERY_PERSON_EMAIL)))
                .withUserAccounts(List.of(Map.of(CLIENT_ID, CLIENT_EMAIL))).withMealKitIdForUser(FIRST_MEAL_KIT_ID, CLIENT_ID)
                .withMealKitIdForUser(TENTH_MEAL_KIT_ID, CLIENT_ID);
        notificationContextInitializer.createNotificationService(eventBus);

        UserContextInitializer userContextInitializer = new UserContextInitializer(eventBus).withClients(List.of(Map.of(CLIENT_ID, CLIENT_REGISTRATION_QUERY)))
            .withCooks(List.of(Map.of(COOK_ID, COOK_REGISTRATION_QUERY))).withShippers(List.of(Map.of(DELIVERY_PERSON_ID, DELIVERY_PERSON_REGISTRATION_QUERY),
                Map.of(SECOND_DELIVERY_PERSON_ID, SECOND_DELIVERY_PERSON_REGISTRATION_QUERY)));

        UserResource userResource = new UserResource(userContextInitializer.createService());
        AuthGuard authGuard = userContextInitializer.createAuthGuard();

        CookingContextInitializer cookingContextInitializer = new CookingContextInitializer().withMealKitsForSubscriber(List.of(NINTH_MEAL_KIT_ORDER),
            CLIENT_ID, Optional.empty()).withMealKitsForSubscriber(List.of(FIRST_MEAL_KIT_ORDER, SECOND_MEAL_KIT_ORDER, THIRD_MEAL_KIT_ORDER,
            FOURTH_MEAL_KIT_ORDER, FIFTH_MEAL_KIT_ORDER, SIXTH_MEAL_KIT_ORDER, SEVENTH_MEAL_KIT_ORDER, EIGHTH_MEAL_KIT_ORDER, TENTH_MEAL_KIT_ORDER),
            CLIENT_ID, Optional.of(DeliveryLocationId.VACHON));
        CookingService cookingService = cookingContextInitializer.createCookingService(eventBus);
        MealKitResource mealKitResource = new MealKitResource(cookingService);
        cookingContextInitializer.createMealKitEventHandler(cookingService, eventBus);

        SubscriptionContextInitializer subscriptionContextInitializer = new SubscriptionContextInitializer().withSubscribers(List.of(SUBSCRIBER))
            .withSubscriptions(List.of(
                new Subscription(FIRST_SUBSCRIPTION_ID, CLIENT_ID, List.of(FIRST_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(),
                    A_SEMESTER, MealKitType.STANDARD),
                new Subscription(SECOND_SUBSCRIPTION_ID, CLIENT_ID, List.of(SECOND_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(),
                    A_SEMESTER, MealKitType.STANDARD),
                new Subscription(THIRD_SUBSCRIPTION_ID, CLIENT_ID, List.of(THIRD_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(),
                    A_SEMESTER, MealKitType.STANDARD),
                new Subscription(FOURTH_SUBSCRIPTION_ID, CLIENT_ID, List.of(FOURTH_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(),
                    A_SEMESTER, MealKitType.STANDARD),
                new Subscription(FIFTH_SUBSCRIPTION_ID, CLIENT_ID, List.of(FIFTH_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(),
                    A_SEMESTER, MealKitType.STANDARD),
                new Subscription(SIXTH_SUBSCRIPTION_ID, CLIENT_ID, List.of(SIXTH_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(),
                    A_SEMESTER, MealKitType.STANDARD),
                new Subscription(SEVENTH_SUBSCRIPTION_ID, CLIENT_ID, List.of(SEVENTH_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID,
                    LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(EIGHTH_SUBSCRIPTION_ID, CLIENT_ID, List.of(EIGHTH_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(),
                    A_SEMESTER, MealKitType.STANDARD),
                new Subscription(NINTH_SUBSCRIPTION_ID, CLIENT_ID, List.of(NINTH_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(),
                    A_SEMESTER, MealKitType.STANDARD),
                new Subscription(TENTH_SUBSCRIPTION_ID, CLIENT_ID, List.of(TENTH_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID, LocalDate.now(),
                    A_SEMESTER, MealKitType.STANDARD)));
        SubscriberService subscriberService = subscriptionContextInitializer.createSubscriberService(eventBus);
        SubscriptionService subscriptionService = subscriptionContextInitializer.createSubscriptionService(eventBus, paymentService);
        AccountResource accountResource = new AccountResource(subscriberService);
        SubscriptionResource subscriptionResource = new SubscriptionResource(subscriptionService);
        RepULJob processConfirmationForTheDayJob = new ProcessConfirmationForTheDayJob(subscriptionService);
        subscriptionContextInitializer.createUserEventHandler(subscriberService, eventBus);

        DeliveryContextInitializer deliveryContextInitializer = new DeliveryContextInitializer().withDeliveryPeople(List.of(DELIVERY_PERSON_ID))
            .withPendingMealKits(List.of(Map.of(FIRST_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID), Map.of(SECOND_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID),
                Map.of(THIRD_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID), Map.of(FOURTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID),
                Map.of(FIFTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID), Map.of(SIXTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID),
                Map.of(SEVENTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID), Map.of(EIGHTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID),
                Map.of(NINTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID), Map.of(TENTH_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID))).withCargo(List.of(TENTH_MEAL_KIT_ID));
        DeliveryService deliveryService = deliveryContextInitializer.createDeliveryService(eventBus);
        CargoResource cargoResource = new CargoResource(deliveryService);
        LocationResource locationResource = new LocationResource(deliveryContextInitializer.createLocationsCatalogService());
        deliveryContextInitializer.createDeliveryEventHandler(deliveryService, eventBus);

        LockerAuthorizationContextInitializer lockerAuthorizationContextInitializer = new LockerAuthorizationContextInitializer().withOrders(
            List.of(Map.entry(CLIENT_ID, FIRST_MEAL_KIT_ID), Map.entry(CLIENT_ID, SECOND_MEAL_KIT_ID), Map.entry(CLIENT_ID, THIRD_MEAL_KIT_ID),
                Map.entry(CLIENT_ID, FOURTH_MEAL_KIT_ID), Map.entry(CLIENT_ID, FIFTH_MEAL_KIT_ID), Map.entry(CLIENT_ID, SIXTH_MEAL_KIT_ID),
                Map.entry(CLIENT_ID, SEVENTH_MEAL_KIT_ID), Map.entry(CLIENT_ID, EIGHTH_MEAL_KIT_ID), Map.entry(CLIENT_ID, NINTH_MEAL_KIT_ID),
                Map.entry(CLIENT_ID, TENTH_MEAL_KIT_ID)));
        LockerAuthorizationService lockerAuthorizationService = lockerAuthorizationContextInitializer.createLockerAuthorizationService(eventBus);
        LockerAuthorizationResource lockerAuthorizationResource = new LockerAuthorizationResource(lockerAuthorizationService);
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
