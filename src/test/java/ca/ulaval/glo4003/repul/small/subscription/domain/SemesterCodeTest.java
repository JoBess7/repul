package ca.ulaval.glo4003.repul.small.subscription.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidSemesterCodeException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SemesterCodeTest {
    private static final String A_VALID_SEMESTER_CODE = "E24";

    @Test
    public void givenNullValue_whenCreatingSemesterCode_shouldThrowInvalidSemesterCode() {
        assertThrows(InvalidSemesterCodeException.class, () -> new SemesterCode(null));
    }

    @Test
    public void givenBlankValue_whenCreatingSemesterCode_shouldThrowInvalidSemesterCode() {
        assertThrows(InvalidSemesterCodeException.class, () -> new SemesterCode(""));
    }

    @Test
    public void givenValidValue_whenCreatingSemesterCode_shouldNotThrow() {
        assertDoesNotThrow(() -> new SemesterCode(A_VALID_SEMESTER_CODE));
    }
}
