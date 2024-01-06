package ca.ulaval.glo4003.repul.small.user.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.User;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.UserRepository;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.UserNotFoundException;
import ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement.InMemoryUserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class InMemoryUserRepositoryTest {
    private static final UniqueIdentifier A_UID = new UniqueIdentifierFactory<>(UniqueIdentifier.class).generate();
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private UserRepository inMemoryUserRepository;

    @Mock
    private User user;

    @BeforeEach
    public void setup() {
        this.inMemoryUserRepository = new InMemoryUserRepository();
    }

    @Test
    public void givenNoUser_whenGettingUserByEmail_shouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> inMemoryUserRepository.findByEmail(AN_EMAIL));
    }

    @Test
    public void whenSavingAndGettingUserByEmail_shouldReturnRightUser() {
        given(user.getEmail()).willReturn(AN_EMAIL);

        inMemoryUserRepository.save(user);
        User userFound = inMemoryUserRepository.findByEmail(AN_EMAIL);

        assertEquals(user, userFound);
    }

    @Test
    public void givenNoUser_whenGettingUserByUid_shouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> inMemoryUserRepository.findByUid(A_UID));
    }

    @Test
    public void whenSavingAndGettingUserByUId_shouldReturnRightUser() {
        given(user.getEmail()).willReturn(AN_EMAIL);
        given(user.getUid()).willReturn(A_UID);

        inMemoryUserRepository.save(user);
        User userFound = inMemoryUserRepository.findByUid(A_UID);

        assertEquals(user, userFound);
    }

    @Test
    public void givenNoUser_whenCheckingIfUserExists_shouldReturnFalse() {
        boolean userExists = inMemoryUserRepository.exists(AN_EMAIL);

        assertFalse(userExists);
    }

    @Test
    public void givenExistingUser_whenCheckingIfUserExists_shouldReturnTrue() {
        given(user.getEmail()).willReturn(AN_EMAIL);
        inMemoryUserRepository.save(user);

        boolean userExists = inMemoryUserRepository.exists(AN_EMAIL);

        assertTrue(userExists);
    }
}
