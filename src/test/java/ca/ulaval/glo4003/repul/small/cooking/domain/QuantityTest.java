package ca.ulaval.glo4003.repul.small.cooking.domain;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.cooking.domain.Quantity;
import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidQuantityUnitException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidQuantityValueException;

import static org.junit.jupiter.api.Assertions.*;

public class QuantityTest {
    private static final int INVALID_VALUE = -1;
    private static final int VALID_VALUE = 1;
    private static final int ANOTHER_VALID_VALUE = 3;
    private static final String VALID_UNIT = "kg";
    private static final String OTHER_UNIT = "lb";
    private static final Quantity A_QUANTITY = new Quantity(VALID_VALUE, VALID_UNIT);

    @Test
    public void givenValueBelowZero_whenCreatingQuantity_shouldThrowInvalidQuantityValueException() {
        assertThrows(InvalidQuantityValueException.class, () -> new Quantity(INVALID_VALUE, VALID_UNIT));
    }

    @Test
    public void givenValidParams_whenCreatingQuantity_shouldNotThrow() {
        assertDoesNotThrow(() -> new Quantity(VALID_VALUE, VALID_UNIT));
    }

    @Test
    public void whenAddingQuantity_shouldAddValues() {
        Quantity quantityToAdd = new Quantity(ANOTHER_VALID_VALUE, VALID_UNIT);

        Quantity addedQuantity = A_QUANTITY.add(quantityToAdd);

        assertEquals(A_QUANTITY.getValue() + quantityToAdd.getValue(), addedQuantity.getValue());
    }

    @Test
    public void givenQuantityWithDifferentUnit_whenAddingQuantity_shouldThrowInvalidQuantityUnitException() {
        Quantity quantityToAdd = new Quantity(ANOTHER_VALID_VALUE, OTHER_UNIT);

        assertThrows(InvalidQuantityUnitException.class, () -> A_QUANTITY.add(quantityToAdd));
    }
}
