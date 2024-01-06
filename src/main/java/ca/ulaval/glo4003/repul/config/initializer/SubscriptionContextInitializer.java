package ca.ulaval.glo4003.repul.config.initializer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.api.UserEventHandler;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriptionRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SubscriptionContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionContextInitializer.class);
    private static final String SEMESTERS_FILE_PATH = "src/main/resources/semesters-232425.json";
    private static final String SEMESTER_CODE_FIELD_NAME_IN_JSON = "semester_code";
    private static final String START_DATE_FIELD_NAME_IN_JSON = "start_date";
    private static final String END_DATE_FIELD_NAME_IN_JSON = "end_date";
    private final SubscriptionFactory subscriptionFactory;
    private final SubscriberFactory subscriberFactory = new SubscriberFactory();
    private SubscriberRepository subscriberRepository = new InMemorySubscriberRepository();
    private SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();

    public SubscriptionContextInitializer() {
        List<Semester> semesters = parseSemesters();
        List<DeliveryLocationId> deliveryLocationIds = new ArrayList<>(EnumSet.allOf(DeliveryLocationId.class));

        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        this.subscriptionFactory = new SubscriptionFactory(mealKitUniqueIdentifierFactory, new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class),
            new OrdersFactory(mealKitUniqueIdentifierFactory), semesters, deliveryLocationIds);
    }

    public SubscriptionContextInitializer withSubscriptionRepository(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
        return this;
    }

    public SubscriptionContextInitializer withSubscriberRepository(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
        return this;
    }

    public SubscriptionContextInitializer withSubscriptions(List<Subscription> subscriptions) {
        subscriptions.forEach(subscriptionRepository::save);
        return this;
    }

    public SubscriptionContextInitializer withSubscribers(List<Subscriber> subscribers) {
        subscribers.forEach(subscriberRepository::save);
        return this;
    }

    public SubscriptionService createSubscriptionService(RepULEventBus eventBus, PaymentService paymentService) {
        LOGGER.info("Creating Subscription service");
        SubscriptionService service = new SubscriptionService(subscriptionRepository, subscriptionFactory, paymentService, eventBus);
        eventBus.register(service);
        return service;
    }

    public SubscriberService createSubscriberService(RepULEventBus eventBus) {
        LOGGER.info("Creating Subscriber service");
        SubscriberService service = new SubscriberService(subscriberRepository, subscriberFactory, eventBus);
        return service;
    }

    public UserEventHandler createUserEventHandler(SubscriberService subscriberService, RepULEventBus eventBus) {
        UserEventHandler userEventHandler = new UserEventHandler(subscriberService);
        eventBus.register(userEventHandler);
        return userEventHandler;
    }

    private List<Semester> parseSemesters() {
        List<Map<String, Object>> listOfSemesterMaps = getListOfMapsFromJsonFilePath(SEMESTERS_FILE_PATH);
        List<Semester> semesters = new ArrayList<>();

        for (Map<String, Object> semesterAsMap : listOfSemesterMaps) {
            String semesterCode = (String) semesterAsMap.get(SEMESTER_CODE_FIELD_NAME_IN_JSON);
            LocalDate startDate = parseDate((String) semesterAsMap.get(START_DATE_FIELD_NAME_IN_JSON));
            LocalDate endDate = parseDate((String) semesterAsMap.get(END_DATE_FIELD_NAME_IN_JSON));

            Semester semester = new Semester(new SemesterCode(semesterCode), startDate, endDate);
            semesters.add(semester);
        }
        return semesters;
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            LOGGER.error("Error while parsing date", e);
            throw new RuntimeException("Error while parsing date");
        }
    }

    private List<Map<String, Object>> getListOfMapsFromJsonFilePath(String filePath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(new File(filePath), new TypeReference<>() {
            });
        } catch (IOException e) {
            LOGGER.error("Error while reading " + filePath, e);
            throw new RuntimeException("Error while reading " + filePath);
        }
    }
}
