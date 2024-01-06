package ca.ulaval.glo4003.repul.small.commons.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.Amount;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidAmountException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AmountTest {
    private static final double A_INVALID_AMOUNT_VALUE = -1.0;
    private static final double A_VALID_AMOUNT_VALUE_NOT_ROUNDED = 1.232342345233451;
    private static final double A_VALID_AMOUNT_VALUE_ROUNDED = 1.23;
    private static final Amount A_VALID_AMOUNT = new Amount(1.23);
    private static final Amount ANOTHER_VALID_AMOUNT = new Amount(4.56);

    @Test
    void givenNegativeValue_whenCreatingAmount_shouldThrowInvalidAmountException() {
        assertThrows(InvalidAmountException.class, () -> new Amount(A_INVALID_AMOUNT_VALUE));
    }

    @Test
    void whenCreatingAmount_shouldRoundToTwoDecimals() {
        Amount amount = new Amount(A_VALID_AMOUNT_VALUE_NOT_ROUNDED);

        assertEquals(A_VALID_AMOUNT_VALUE_ROUNDED, amount.getValue());
    }

    @Test
    void whenAddingAmount_shouldReturnSum() {
        Amount sum = A_VALID_AMOUNT.add(ANOTHER_VALID_AMOUNT);

        assertEquals(5.79, sum.getValue());
    }

    @Test
    void whenMultiplyingAmount_shouldReturnProductRounded() {
        double multiplier = 4.56;

        Amount product = A_VALID_AMOUNT.multiply(multiplier);

        assertEquals(5.61, product.getValue());
    }

    @Test
    void whenSubtractingAmount_shouldReturnDifference() {
        Amount difference = ANOTHER_VALID_AMOUNT.subtract(A_VALID_AMOUNT);

        assertEquals(3.33, difference.getValue());
    }
}
