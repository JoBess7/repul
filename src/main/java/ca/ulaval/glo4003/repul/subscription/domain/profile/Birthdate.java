package ca.ulaval.glo4003.repul.subscription.domain.profile;

import java.time.LocalDate;
import java.time.Period;

import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidBirthdateException;

public record Birthdate(
    LocalDate value
) {
    public Birthdate {
        if (value.isAfter(LocalDate.now())) {
            throw new InvalidBirthdateException();
        }
    }

    public int getAge() {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(value, currentDate);
        return period.getYears();
    }
}
