package ca.ulaval.glo4003.repul.small.user.domain.identitymanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Password;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.PasswordEncoder;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserFactory;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserFactoryTest {
    private static final UniqueIdentifier A_UID = new UniqueIdentifierFactory<>(UniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final Password A_PASSWORD = new Password("a@*nfF8KA1");
    private static final Password AN_ENCRYPTED_PASSWORD = new Password("encryptedPassword");

    private UserFactory userFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userFactory = new UserFactory(passwordEncoder);
    }

    @Test
    public void whenCreatingUser_shouldEncryptPassword() {
        given(passwordEncoder.encode(A_PASSWORD)).willReturn(AN_ENCRYPTED_PASSWORD);

        userFactory.createUser(A_UID, AN_IDUL, AN_EMAIL, Role.CLIENT, A_PASSWORD);

        verify(passwordEncoder).encode(A_PASSWORD);
    }
}
