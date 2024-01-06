package ca.ulaval.glo4003.repul.small.user.domain.identitymanagement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.PasswordEncoder;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.PasswordNotMatchingException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserTest {
    private static final UniqueIdentifier A_UID = new UniqueIdentifierFactory<>(UniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final Password A_NOT_ENCRYPTED_PASSWORD = new Password("aNotEncryptedPassword");
    private static final Password AN_ENCRYPTED_PASSWORD = new Password(A_NOT_ENCRYPTED_PASSWORD.value());
    private static final Password A_WRONG_PASSWORD = new Password("a@*nfF8KA1");

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void whenCheckingPassword_shouldMatchPassword() {
        given(passwordEncoder.matches(A_NOT_ENCRYPTED_PASSWORD, AN_ENCRYPTED_PASSWORD)).willReturn(true);
        User user = new User(A_UID, AN_IDUL, AN_EMAIL, AN_ENCRYPTED_PASSWORD, Role.CLIENT);

        user.checkPasswordMatches(passwordEncoder, A_NOT_ENCRYPTED_PASSWORD);

        verify(passwordEncoder).matches(A_NOT_ENCRYPTED_PASSWORD, AN_ENCRYPTED_PASSWORD);
    }

    @Test
    public void givenIncorrectPassword_whenCheckingPassword_shouldThrowPasswordNotMatchingException() {
        given(passwordEncoder.matches(A_WRONG_PASSWORD, AN_ENCRYPTED_PASSWORD)).willReturn(false);
        User user = new User(A_UID, AN_IDUL, AN_EMAIL, AN_ENCRYPTED_PASSWORD, Role.CLIENT);

        assertThrows(PasswordNotMatchingException.class, () -> user.checkPasswordMatches(passwordEncoder, A_WRONG_PASSWORD));
    }
}
