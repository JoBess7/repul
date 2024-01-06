package ca.ulaval.glo4003.repul.small.delivery.application.payload;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.delivery.application.payload.LockerPayload;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LockerPayloadTest {
    private static final String LOCKER_ID = "a locker id";

    @Test
    public void givenLockerId_whenUsingFrom_shouldReturnCorrectLockerPayload() {
        LockerPayload expectedLockerPayload = new LockerPayload(LOCKER_ID);
        LockerId lockerId = new LockerId(LOCKER_ID, 1);

        LockerPayload actualLockerPayload = LockerPayload.from(lockerId);

        assertEquals(expectedLockerPayload, actualLockerPayload);
    }
}
