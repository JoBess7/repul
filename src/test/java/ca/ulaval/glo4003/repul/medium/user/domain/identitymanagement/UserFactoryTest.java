package ca.ulaval.glo4003.repul.medium.user.domain.identitymanagement;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.PasswordEncoder;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserFactory;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.CryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserFactoryTest {
    private static final UniqueIdentifier A_UID = new UniqueIdentifierFactory<>(UniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final Password A_PASSWORD = new Password("a@*nfF8KA1");

    @Test
    public void whenCreatingUser_shouldSavePasswordCorrectly() {
        PasswordEncoder passwordEncoder = new CryptPasswordEncoder();
        UserFactory userFactory = new UserFactory(passwordEncoder);

        User user = userFactory.createUser(A_UID, AN_IDUL, AN_EMAIL, Role.CLIENT, A_PASSWORD);

        assertDoesNotThrow(() -> user.checkPasswordMatches(passwordEncoder, A_PASSWORD));
    }
}
