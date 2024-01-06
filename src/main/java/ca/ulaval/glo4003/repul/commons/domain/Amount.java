package ca.ulaval.glo4003.repul.commons.domain;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidAmountException;

public record Amount(
    double value
) {
    public Amount(double value) {
        if (value < 0) {
            throw new InvalidAmountException();
        }
        this.value = round(value);
    }

    public double getValue() {
        return value;
    }

    public Amount add(Amount amount) {
        return new Amount(value + amount.value);
    }

    public Amount multiply(double multiplier) {
        return new Amount(value * multiplier);
    }

    public Amount subtract(Amount amount) {
        return new Amount(value - amount.value);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
