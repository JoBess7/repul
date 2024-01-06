package ca.ulaval.glo4003.repul.small.commons.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidIDULException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IDULTest {
    private static final String AN_EMPTY_IDUL = "";
    private static final String AN_IDUL_STARTING_WITH_MORE_THAN_FIVE_LETTERS = "ALMATHBOB69";
    private static final String AN_IDUL_STARTING_WITH_LESS_THAN_TWO_LETTRES = "A69";
    private static final String AN_IDUL_ENDING_WITH_MORE_THAN_THREE_NUMBERS = "ALMATH69422";
    private static final String AN_IDUL_ENDING_WITH_LESS_THAN_ONE_NUMBER = "ALMATH";
    private static final String AN_IDUL_ENDING_WITH_NUMBERS_STARTING_WITH_ZERO = "ALMA069";
    private static final String AN_IDUL_WITH_LETTERS_IN_LOWER_CASE = "almath69";
    private static final String A_VALID_IDUL = "ALMAT69";

    @Test
    public void givenEmptyValue_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_EMPTY_IDUL));
    }

    @Test
    public void givenValueStartingWithMoreThanFiveLetters_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_IDUL_STARTING_WITH_MORE_THAN_FIVE_LETTERS));
    }

    @Test
    public void givenValueStartingWithLessThanTwoLetters_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_IDUL_STARTING_WITH_LESS_THAN_TWO_LETTRES));
    }

    @Test
    public void givenValueEndingWithMoreThanThreeNumbers_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_IDUL_ENDING_WITH_MORE_THAN_THREE_NUMBERS));
    }

    @Test
    public void givenValueEndingWithLessThanOneNumber_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_IDUL_ENDING_WITH_LESS_THAN_ONE_NUMBER));
    }

    @Test
    public void givenValueEndingWithNumbersStartingWithZero_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_IDUL_ENDING_WITH_NUMBERS_STARTING_WITH_ZERO));
    }

    @Test
    public void givenValueWithLettersInLowerCase_whenCreatingIDUL_shouldThrowInvalidIDULException() {
        assertThrows(InvalidIDULException.class, () -> new IDUL(AN_IDUL_WITH_LETTERS_IN_LOWER_CASE));
    }

    @Test
    public void givenValidValue_whenCreatingIDUL_shouldNotThrowInvalidIDULException() {
        IDUL idul = new IDUL(A_VALID_IDUL);

        assertEquals(A_VALID_IDUL, idul.value());
    }
}
