package ca.ulaval.glo4003.repul.fixture.subscription;

import java.time.LocalDate;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

public class SubscriberFixture {
    private SubscriberUniqueIdentifier subscriberId;
    private IDUL idul;
    private Name name;
    private Birthdate birthdate;
    private Gender gender;
    private Email email;
    private Optional<UserCardNumber> cardNumber = Optional.empty();

    public SubscriberFixture() {
        subscriberId = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
        idul = new IDUL("ALMAT69");
        name = new Name("John Doe");
        birthdate = new Birthdate(LocalDate.now().minusYears(18));
        gender = Gender.OTHER;
        email = new Email("email@ulaval.ca");
    }

    public SubscriberFixture withSubscriberId(SubscriberUniqueIdentifier subscriberId) {
        this.subscriberId = subscriberId;
        return this;
    }

    public SubscriberFixture withIdul(IDUL idul) {
        this.idul = idul;
        return this;
    }

    public SubscriberFixture withName(Name name) {
        this.name = name;
        return this;
    }

    public SubscriberFixture withBirthdate(Birthdate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public SubscriberFixture withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public SubscriberFixture withEmail(Email email) {
        this.email = email;
        return this;
    }

    public SubscriberFixture withCardNumber(UserCardNumber cardNumber) {
        this.cardNumber = Optional.of(cardNumber);
        return this;
    }

    public Subscriber build() {
        Subscriber subscriber = new Subscriber(subscriberId, idul, name, birthdate, gender, email);
        cardNumber.ifPresent(subscriber::setCardNumber);

        return subscriber;
    }
}
