package ca.ulaval.glo4003.repul.small.lockerauthorization.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.lockerauthorization.application.exception.LockerAuthorizationSystemNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.infrastructure.InMemoryLockerAuthorizationSystemRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InMemoryLockerAuthorizationSystemRepositoryTest {
    @Mock
    private LockerAuthorizationSystem lockerAuthorizationSystem;
    private InMemoryLockerAuthorizationSystemRepository inMemoryLockerAuthorizationSystemRepository;

    @BeforeEach
    public void createRepo() {
        inMemoryLockerAuthorizationSystemRepository = new InMemoryLockerAuthorizationSystemRepository();
    }

    @Test
    public void whenSavingAndGettingLockerAuthorizationSystem_shouldReturnTheLockerAuthorizationSystem() {
        inMemoryLockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
        LockerAuthorizationSystem lockerAuthorizationSystemFound = inMemoryLockerAuthorizationSystemRepository.get();

        assertEquals(lockerAuthorizationSystem, lockerAuthorizationSystemFound);
    }

    @Test
    public void givenNoLockerAuthorizationSystem_whenGettingLockerAuthorizationSystem_shouldThrowLockerAuthorizationSystemNotFoundException() {
        assertThrows(LockerAuthorizationSystemNotFoundException.class, () -> inMemoryLockerAuthorizationSystemRepository.get());
    }
}
