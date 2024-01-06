package ca.ulaval.glo4003.repul.user.application;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.query.LoginQuery;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.PasswordEncoder;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserRepository;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.IDULAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenGenerator;

public class UserService {
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final UniqueIdentifierFactory<SubscriberUniqueIdentifier> subscriberUniqueIdentifierFactory;
    private final TokenGenerator tokenGenerator;
    private final RepULEventBus eventBus;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserFactory userFactory,
                       UniqueIdentifierFactory<SubscriberUniqueIdentifier> subscriberUniqueIdentifierFactory, TokenGenerator tokenGenerator,
                       PasswordEncoder passwordEncoder, RepULEventBus eventBus) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.subscriberUniqueIdentifierFactory = subscriberUniqueIdentifierFactory;
        this.tokenGenerator = tokenGenerator;
        this.eventBus = eventBus;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegistrationQuery registrationQuery) {
        if (userRepository.exists(registrationQuery.email())) {
            throw new EmailAlreadyInUseException();
        }
        if (userRepository.exists(registrationQuery.idul())) {
            throw new IDULAlreadyInUseException();
        }
        UniqueIdentifier uniqueIdentifier = subscriberUniqueIdentifierFactory.generate();
        User newUser = userFactory.createUser(uniqueIdentifier, registrationQuery.idul(), registrationQuery.email(), Role.CLIENT, registrationQuery.password());
        userRepository.save(newUser);

        sendUserCreatedEvent(registrationQuery, (SubscriberUniqueIdentifier) uniqueIdentifier);
    }

    public Token login(LoginQuery loginQuery) {
        try {
            User user = userRepository.findByEmail(loginQuery.email());

            user.checkPasswordMatches(passwordEncoder, loginQuery.password());

            Token token = tokenGenerator.generate(user.getUid(), user.getRole());

            return token;
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
    }

    private void sendUserCreatedEvent(RegistrationQuery registrationQuery, SubscriberUniqueIdentifier createdUserId) {
        UserCreatedEvent event =
            new UserCreatedEvent(createdUserId, registrationQuery.idul(), registrationQuery.name(), registrationQuery.birthdate(), registrationQuery.gender(),
                registrationQuery.email());
        eventBus.publish(event);
    }
}
