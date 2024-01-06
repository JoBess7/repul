package ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.PasswordEncoder;

public class CryptPasswordEncoder implements PasswordEncoder {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Password encode(Password password) {
        return new Password(encoder.encode(password.value()));
    }

    @Override
    public boolean matches(Password providedPassword, Password encodedPassword) {
        return encoder.matches(providedPassword.value(), encodedPassword.value());
    }
}
