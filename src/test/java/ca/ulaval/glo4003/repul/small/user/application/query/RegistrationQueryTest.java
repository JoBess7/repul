package ca.ulaval.glo4003.repul.small.user.application.query;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrationQueryTest {
    private static final Email AN_EMAIL = new Email("email@ulaval.ca");
    private static final Password A_PASSWORD = new Password("password");
    private static final IDUL AN_IDUL = new IDUL("ABC123");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.parse("1999-01-01"));
    private static final Gender A_GENDER = Gender.MAN;

    @Test
    public void whenUsingFrom_shouldCreateRegistrationQuery() {
        RegistrationQuery expectedRegistrationQuery = new RegistrationQuery(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER);

        RegistrationQuery actualRegisterQuery =
            RegistrationQuery.from(AN_EMAIL.value(), A_PASSWORD.value(), AN_IDUL.value(), A_NAME.value(), A_BIRTHDATE.value().toString(), A_GENDER.name());

        assertEquals(expectedRegistrationQuery, actualRegisterQuery);
    }

    @Test
    public void givenLowerCaseIDUL_whenUsingFrom_shouldTransformToUpperCase() {
        RegistrationQuery expectedRegistrationQuery = new RegistrationQuery(AN_EMAIL, A_PASSWORD, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER);
        String aLowerCaseIDUL = AN_IDUL.value().toLowerCase();

        RegistrationQuery actualRegisterQuery =
            RegistrationQuery.from(AN_EMAIL.value(), A_PASSWORD.value(), aLowerCaseIDUL, A_NAME.value(), A_BIRTHDATE.value().toString(), A_GENDER.name());

        assertEquals(expectedRegistrationQuery, actualRegisterQuery);
    }
}
