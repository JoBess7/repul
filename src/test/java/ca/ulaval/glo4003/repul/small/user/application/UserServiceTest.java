package ca.ulaval.glo4003.repul.small.user.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.application.UserService;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.query.LoginQuery;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.PasswordEncoder;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserRepository;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.IDULAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenGenerator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final String AN_IDUL = "ALMAT69";
    private static final String AN_EMAIL = "john@ulaval.ca";
    private static final String A_PASSWORD = "a@*nfF8KA1";
    private static final String A_NAME = "aName";
    private static final String A_BIRTHDATE = "2000-01-01";
    private static final String A_GENDER = "MAN";
    private static final String A_CARD_NUMBER = "123456789";
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UniqueIdentifierFactory uniqueIdentifierFactory;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private RepULEventBus repULEventBus;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository, userFactory, uniqueIdentifierFactory, tokenGenerator, passwordEncoder, repULEventBus);
    }

    @Test
    public void givenRegistrationQueryWithExistingEmail_whenRegistering_shouldThrowEmailAlreadyInUseException() {
        given(userRepository.exists(any(Email.class))).willReturn(true);

        assertThrows(EmailAlreadyInUseException.class,
            () -> userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER)));
    }

    @Test
    public void givenRegistrationQueryWithExistingIDUL_whenRegistering_shouldThrowIDULAlreadyInUseException() {
        given(userRepository.exists(any(Email.class))).willReturn(false);
        given(userRepository.exists(any(IDUL.class))).willReturn(true);

        assertThrows(IDULAlreadyInUseException.class,
            () -> userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER)));
    }

    @Test
    public void givenUserNotFound_whenLogin_shouldThrowInvalidCredentialsException() {
        given(userRepository.findByEmail(new Email(AN_EMAIL))).willReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(LoginQuery.from(AN_EMAIL, A_PASSWORD)));
    }

    @Test
    public void whenRegistering_shouldPublishUserCreatedEvent() {
        given(userRepository.exists(any(Email.class))).willReturn(false);
        given(userRepository.exists(any(IDUL.class))).willReturn(false);
        given(userFactory.createUser(any(), any(), any(), any(), any())).willReturn(mock(User.class));

        userService.register(RegistrationQuery.from(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER));

        verify(repULEventBus).publish(any(UserCreatedEvent.class));
    }
}
