package ca.ulaval.glo4003.repul.small.subscription.domain.profile;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidNameException;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NameTest {
    private static final String A_BASED_NAME = "Alex Mathieu";

    @Test
    public void givenBlankValue_whenCreatingName_shouldThrowInvalidNameException() {
        assertThrows(InvalidNameException.class, () -> new Name(""));
    }

    @Test
    public void givenValidValue_whenCreatingLocationId_shouldNotThrow() {
        assertDoesNotThrow(() -> new Name(A_BASED_NAME));
    }
}
