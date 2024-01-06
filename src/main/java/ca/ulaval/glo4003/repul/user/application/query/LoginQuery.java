package ca.ulaval.glo4003.repul.user.application.query;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;

public record LoginQuery(
    Email email,
    Password password
) {
    public static LoginQuery from(String email, String password) {
        return new LoginQuery(new Email(email), new Password(password));
    }
}
