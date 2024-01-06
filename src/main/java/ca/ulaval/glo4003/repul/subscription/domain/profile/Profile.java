package ca.ulaval.glo4003.repul.subscription.domain.profile;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;

public class Profile {
    private final IDUL idul;
    private final Name name;
    private final Birthdate birthdate;
    private final Gender gender;
    private final Email email;
    private Optional<UserCardNumber> cardNumber = Optional.empty();

    public Profile(IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        this.idul = idul;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
    }

    public IDUL getIdul() {
        return idul;
    }

    public Name getName() {
        return name;
    }

    public Birthdate getBirthdate() {
        return birthdate;
    }

    public int getAge() {
        return birthdate.getAge();
    }

    public Gender getGender() {
        return gender;
    }

    public Email getEmail() {
        return email;
    }

    public Optional<UserCardNumber> getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(UserCardNumber cardNumber) {
        this.cardNumber = Optional.of(cardNumber);
    }
}
