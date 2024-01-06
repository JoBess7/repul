package ca.ulaval.glo4003.repul.small.user.domain.identitymanagement;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidPasswordException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PasswordTest {
    private static final String AN_EMPTY_PASSWORD = "";
    private static final String A_VALID_PASSWORD = "aPassword";

    @Test
    public void givenEmptyValue_whenCreatingPassword_shouldThrowInvalidPasswordException() {
        assertThrows(InvalidPasswordException.class, () -> new Password(AN_EMPTY_PASSWORD));
    }

    @Test
    public void givenValidValue_whenCreatingPassword_shouldNotThrowInvalidPasswordException() {
        Password password = new Password(A_VALID_PASSWORD);

        assertEquals(A_VALID_PASSWORD, password.value());
    }
}
