package ca.ulaval.glo4003.repul.cooking.domain;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidQuantityUnitException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidQuantityValueException;

public class Quantity {
    private final double value;
    private final String unit;

    public Quantity(double value, String unit) {
        if (value <= 0) {
            throw new InvalidQuantityValueException();
        }
        this.value = value;
        this.unit = unit;
    }

    public Quantity(String quantity) {
        Pattern pattern = Pattern.compile("([0-9]+)\\s*([^0-9]*)$");
        Matcher matcher = pattern.matcher(quantity.trim());

        String[] result = new String[2];
        if (matcher.find()) {
            result[0] = matcher.group(1);
            result[1] = matcher.group(2);
        } else {
            result[0] = quantity.trim();
            result[1] = "";
        }

        double parsedValue = Double.parseDouble(result[0]);
        if (parsedValue <= 0) {
            throw new InvalidQuantityValueException();
        }

        this.value = parsedValue;
        this.unit = result[1];
    }

    public Quantity add(Quantity quantityToAdd) {
        if (unit.equals(quantityToAdd.getUnit())) {
            return new Quantity(this.value + quantityToAdd.getValue(), unit);
        }
        throw new InvalidQuantityUnitException();
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quantity)) {
            return false;
        }
        Quantity quantity = (Quantity) o;
        return Double.compare(quantity.value, value) == 0 && Objects.equals(unit, quantity.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }
}
