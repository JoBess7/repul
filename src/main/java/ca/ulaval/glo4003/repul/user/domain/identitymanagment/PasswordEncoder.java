package ca.ulaval.glo4003.repul.user.domain.identitymanagment;

public interface PasswordEncoder {
    Password encode(Password password);

    boolean matches(Password providedPassword, Password encodedPassword);
}
