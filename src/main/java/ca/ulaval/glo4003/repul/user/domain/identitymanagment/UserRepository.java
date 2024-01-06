package ca.ulaval.glo4003.repul.user.domain.identitymanagment;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public interface UserRepository {
    User findByEmail(Email email);

    User findByUid(UniqueIdentifier uid);

    boolean exists(Email email);

    boolean exists(IDUL idul);

    void save(User user);
}
