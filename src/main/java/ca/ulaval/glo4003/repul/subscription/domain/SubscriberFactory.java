package ca.ulaval.glo4003.repul.subscription.domain;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

public class SubscriberFactory {
    public Subscriber createSubscriber(SubscriberUniqueIdentifier subscriberId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        return new Subscriber(subscriberId, idul, name, birthdate, gender, email);
    }
}
