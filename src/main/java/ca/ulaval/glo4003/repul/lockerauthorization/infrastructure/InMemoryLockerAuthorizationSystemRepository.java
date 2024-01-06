package ca.ulaval.glo4003.repul.lockerauthorization.infrastructure;

import java.util.Optional;

import ca.ulaval.glo4003.repul.lockerauthorization.application.exception.LockerAuthorizationSystemNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;

public class InMemoryLockerAuthorizationSystemRepository implements LockerAuthorizationSystemRepository {
    private Optional<LockerAuthorizationSystem> lockerAuthorizationSystem = Optional.empty();

    @Override
    public void save(LockerAuthorizationSystem lockerAuthorizationSystem) {
        this.lockerAuthorizationSystem = Optional.of(lockerAuthorizationSystem);
    }

    @Override
    public LockerAuthorizationSystem get() {
        return lockerAuthorizationSystem.orElseThrow(LockerAuthorizationSystemNotFoundException::new);
    }
}
