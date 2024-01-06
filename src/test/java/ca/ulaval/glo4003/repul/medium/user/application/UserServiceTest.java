package ca.ulaval.glo4003.repul.medium.user.application;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.user.application.UserService;
import ca.ulaval.glo4003.repul.user.application.query.LoginQuery;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.IDULAlreadyInUseException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenDecoder;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.CryptPasswordEncoder;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.InMemoryUserRepository;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.JWTTokenDecoder;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.JWTTokenGenerator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest {
    private static final Email AN_EMAIL = new Email("test420@ulaval.ca");
    private static final Email A_NEW_EMAIL = new Email("aNewMail@ulaval.ca");
    private static final Password A_PASSWORD = new Password("password");
    private static final Password A_WRONG_PASSWORD = new Password("wrongPassword");
    private static final Password A_NEW_PASSWORD = new Password("newPassword");
    private static final IDUL AN_IDUL = new IDUL("TEST420");
    private static final IDUL A_NEW_IDUL = new IDUL("TEST421");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.now().minusYears(22));
    private static final Gender A_GENDER = Gender.MAN;
    private TokenDecoder tokenDecoder;
    private UserService userService;

    @BeforeEach
    public void createUserService() {
        tokenDecoder = new JWTTokenDecoder();
        userService = new UserService(new InMemoryUserRepository(), new UserFactory(new CryptPasswordEncoder()),
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class), new JWTTokenGenerator(), new CryptPasswordEncoder(), new GuavaEventBus());
    }

    @Test
    public void givenRegisteredUser_whenLoggingIn_shouldLogIn() {
        registerUser();

        assertDoesNotThrow(() -> userService.login(new LoginQuery(AN_EMAIL, A_PASSWORD)));
    }

    @Test
    public void givenRegisteredUser_whenLoggingIn_shouldCreateValidToken() {
        registerUser();

        Token token = userService.login(new LoginQuery(AN_EMAIL, A_PASSWORD));

        assertDoesNotThrow(() -> tokenDecoder.decode(token.value()).userId());
    }

    @Test
    public void givenRegistrationQueryWithExistingEmail_whenRegistering_shouldNotSaveNewUser() {
        registerUser();
        RegistrationQuery alreadyRegisteredUserRegistrationQuery = new RegistrationQuery(AN_EMAIL, A_NEW_PASSWORD, A_NEW_IDUL, A_NAME, A_BIRTHDATE, A_GENDER);

        try {
            userService.register(alreadyRegisteredUserRegistrationQuery);
        } catch (EmailAlreadyInUseException ignored) {
            // Ignoring the exception because we want to check the repositories
        }

        assertThrows(InvalidCredentialsException.class, () -> userService.login(new LoginQuery(AN_EMAIL, A_NEW_PASSWORD)));
        assertDoesNotThrow(() -> userService.login(new LoginQuery(AN_EMAIL, A_PASSWORD)));
    }

    @Test
    public void givenRegistrationQueryWithExistingIDUL_whenRegistering_shouldNotSaveNewUser() {
        registerUser();
        RegistrationQuery alreadyRegisteredUserRegistrationQuery = new RegistrationQuery(A_NEW_EMAIL, A_NEW_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER);

        try {
            userService.register(alreadyRegisteredUserRegistrationQuery);
        } catch (IDULAlreadyInUseException ignored) {
            // Ignoring the exception because we want to check the repositories
        }

        assertThrows(InvalidCredentialsException.class, () -> userService.login(new LoginQuery(AN_EMAIL, A_NEW_PASSWORD)));
        assertDoesNotThrow(() -> userService.login(new LoginQuery(AN_EMAIL, A_PASSWORD)));
    }

    @Test
    public void whenLogin_shouldCheckPassword() {
        registerUser();

        assertThrows(InvalidCredentialsException.class, () -> userService.login(new LoginQuery(AN_EMAIL, A_WRONG_PASSWORD)));
    }

    private void registerUser() {
        RegistrationQuery registrationQuery = new RegistrationQuery(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER);
        userService.register(registrationQuery);
    }
}
