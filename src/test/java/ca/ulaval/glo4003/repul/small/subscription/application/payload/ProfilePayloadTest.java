package ca.ulaval.glo4003.repul.small.subscription.application.payload;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriberFixture;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfilePayloadTest {
    private static final String A_NAME = "John Doe";
    private static final String A_BIRTHDATE = "1990-01-01";
    private static final String A_GENDER = "MAN";
    private static final int AN_AGE = LocalDate.now().getYear() - 1990;
    private static final String AN_IDUL = "ALMAT69";
    private static final String AN_EMAIL = "anEmail@ulaval.ca";

    @Test
    public void whenUsingFrom_shouldReturnCorrectProfilePayload() {
        ProfilePayload expectedProfilePayload = new ProfilePayload(A_NAME, A_BIRTHDATE, A_GENDER, AN_AGE, AN_IDUL, AN_EMAIL, null);
        Subscriber subscriber = new SubscriberFixture().withIdul(new IDUL(AN_IDUL)).withName(new Name(A_NAME)).withEmail(new Email(AN_EMAIL))
            .withBirthdate(new Birthdate(LocalDate.parse(A_BIRTHDATE))).withGender(Gender.from(A_GENDER)).build();

        ProfilePayload actualProfilePayload = ProfilePayload.from(subscriber.getProfile());

        assertEquals(expectedProfilePayload, actualProfilePayload);
    }
}
