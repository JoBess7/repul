package ca.ulaval.glo4003.repul.user.domain.identitymanagment.token;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;

public record DecodedToken(UniqueIdentifier userId, Role role) {
}
