package ca.ulaval.glo4003.repul.user.domain.identitymanagment;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.PasswordNotMatchingException;

public class User {
    private final UniqueIdentifier uid;
    private final IDUL idul;
    private final Email email;
    private final Password password;

    private final Role role;

    public User(UniqueIdentifier uid, IDUL idul, Email email, Password password, Role role) {
        this.uid = uid;
        this.idul = idul;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public UniqueIdentifier getUid() {
        return uid;
    }

    public IDUL getIdul() {
        return idul;
    }

    public Email getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public void checkPasswordMatches(PasswordEncoder passwordEncoder, Password providedPassword) {
        if (!passwordEncoder.matches(providedPassword, password)) {
            throw new PasswordNotMatchingException();
        }
    }
}
