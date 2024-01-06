package ca.ulaval.glo4003.repul.lockerauthorization.domain;

import ca.ulaval.glo4003.repul.lockerauthorization.domain.exception.InvalidLockerIdException;

public record LockerId(
    String id
) {
    public LockerId {
        if (id == null || id.isBlank()) {
            throw new InvalidLockerIdException();
        }
    }
}
