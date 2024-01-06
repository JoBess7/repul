package ca.ulaval.glo4003.repul.user.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

public class UserCreatedEvent extends RepULEvent {
    public final UniqueIdentifier userId;
    public final IDUL idul;
    public final Name name;
    public final Birthdate birthdate;
    public final Gender gender;
    public final Email email;

    public UserCreatedEvent(UniqueIdentifier userId, IDUL idul, Name name, Birthdate birthdate, Gender gender, Email email) {
        super();
        this.userId = userId;
        this.idul = idul;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
    }
}
