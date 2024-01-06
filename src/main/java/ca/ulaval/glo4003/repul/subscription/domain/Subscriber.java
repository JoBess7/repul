package ca.ulaval.glo4003.repul.subscription.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Profile;

public class Subscriber {
    private final SubscriberUniqueIdentifier subscriberId;
    private final Profile profile;
    private final Map<SubscriptionUniqueIdentifier, Subscription> subscriptions = new HashMap<>();

    public Subscriber(SubscriberUniqueIdentifier subscriberId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        this.subscriberId = subscriberId;
        this.profile = new Profile(idul, name, birthdate, gender, email);
    }

    public SubscriberUniqueIdentifier getSubscriberId() {
        return subscriberId;
    }

    public Profile getProfile() {
        return profile;
    }

    public Optional<UserCardNumber> getCardNumber() {
        return profile.getCardNumber();
    }

    public void setCardNumber(UserCardNumber cardNumber) {
        profile.setCardNumber(cardNumber);
    }
}
