package ca.ulaval.glo4003.repul.small.commons.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidEmailException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {
    private static final String AN_EMPTY_EMAIL = "";
    private static final String AN_INVALID_EMAIL = "this.my.email.com";
    private static final String AN_EMAIL_WITHOUT_USERNAME = "@ulaval.ca";
    private static final String AN_EMAIL_WITH_TWO_AT = "anEmail123@something@ulaval.ca";
    private static final String AN_EMAIL_WITHOUT_DOMAIN = "anEmail123@";
    private static final String A_VALID_ULAVAL_EMAIL = "anEmail123@ulaval.ca";
    private static final String A_VALID_NON_ULAVAL_EMAIL = "anEmail123@site.com";
    private static final String A_WRONG_DOMAIN_ULAVAL_EMAIL = "anEmailYolo@ulaval.com";
    private static final String A_COMPLEX_ULAVAL_EMAIL = "anEmail123.otherPart+admin@ulaval.ca";

    @Test
    public void givenEmptyValue_whenCreatingEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class, () -> new Email(AN_EMPTY_EMAIL));
    }

    @Test
    public void givenInvalidValue_whenCreatingEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class, () -> new Email(AN_INVALID_EMAIL));
    }

    @Test
    public void givenEmailWithoutUsernameValue_whenCreatingEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class, () -> new Email(AN_EMAIL_WITHOUT_USERNAME));
    }

    @Test
    public void givenEmailWithTwoAtValue_whenCreatingEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class, () -> new Email(AN_EMAIL_WITH_TWO_AT));
    }

    @Test
    public void givenEmailWithoutDomainValue_whenCreatingEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class, () -> new Email(AN_EMAIL_WITHOUT_DOMAIN));
    }

    @Test
    public void givenValidNonUlavalValue_whenCreatingEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class, () -> new Email(A_VALID_NON_ULAVAL_EMAIL));
    }

    @Test
    public void givenUlavalValueWithWrongDomain_whenCreatingEmail_shouldThrowInvalidEmailException() {
        assertThrows(InvalidEmailException.class, () -> new Email(A_WRONG_DOMAIN_ULAVAL_EMAIL));
    }

    @Test
    public void givenValidUlavalValue_whenCreatingEmail_shouldNotThrowInvalidEmailException() {
        Email email = new Email(A_VALID_ULAVAL_EMAIL);

        assertEquals(A_VALID_ULAVAL_EMAIL, email.value());
    }

    @Test
    public void givenValidComplexUlavalValue_whenCreatingEmail_shouldNotThrowInvalidEmailException() {
        Email email = new Email(A_COMPLEX_ULAVAL_EMAIL);

        assertEquals(A_COMPLEX_ULAVAL_EMAIL, email.value());
    }
}
