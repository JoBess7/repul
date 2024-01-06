package ca.ulaval.glo4003.repul.user.domain.identitymanagment.token;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;

public interface TokenGenerator {
    Token generate(UniqueIdentifier uid, Role role);
}
