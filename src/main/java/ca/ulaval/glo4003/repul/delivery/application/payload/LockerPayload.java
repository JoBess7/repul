package ca.ulaval.glo4003.repul.delivery.application.payload;

import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

public record LockerPayload(
    String lockerId
) {
    public static LockerPayload from(LockerId lockerId) {
        return new LockerPayload(lockerId.id());
    }
}
