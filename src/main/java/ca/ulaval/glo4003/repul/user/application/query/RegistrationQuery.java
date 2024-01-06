package ca.ulaval.glo4003.repul.user.application.query;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.user.application.exception.InvalidDateException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;

public record RegistrationQuery(
    Email email,
    Password password,
    IDUL idul,
    Name name,
    Birthdate birthdate,
    Gender gender
) {
    public static RegistrationQuery from(String email, String password, String idul, String name, String birthdate, String gender) {
        try {
            LocalDate.parse(birthdate);
        } catch (DateTimeParseException e) {
            throw new InvalidDateException();
        }
        IDUL parsedIdul = new IDUL(idul.toUpperCase());
        Birthdate parsedBirthdate = new Birthdate(LocalDate.parse(birthdate));

        return new RegistrationQuery(new Email(email), new Password(password), parsedIdul, new Name(name), parsedBirthdate, Gender.from(gender));
    }
}
