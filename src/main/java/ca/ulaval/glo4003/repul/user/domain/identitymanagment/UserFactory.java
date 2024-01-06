package ca.ulaval.glo4003.repul.user.domain.identitymanagment;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class UserFactory {
    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UniqueIdentifier uid, IDUL idul, Email email, Role role, Password password) {
        return new User(uid, idul, email, encryptPassword(password), role);
    }

    private Password encryptPassword(Password password) {
        return passwordEncoder.encode(password);
    }
}
