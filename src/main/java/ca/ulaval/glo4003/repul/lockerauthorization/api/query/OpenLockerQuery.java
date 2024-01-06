package ca.ulaval.glo4003.repul.lockerauthorization.api.query;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;

public record OpenLockerQuery(
    UserCardNumber userCardNumber,
    LockerId lockerId
) {
    public static OpenLockerQuery from(String userCardNumber, String lockerId) {
        return new OpenLockerQuery(
            new UserCardNumber(userCardNumber),
            new LockerId(lockerId));
    }
}
