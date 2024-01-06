package ca.ulaval.glo4003.repul.fixture.lockerauthorization;

import ca.ulaval.glo4003.repul.lockerauthorization.api.request.OpenLockerRequest;

public class OpenLockerRequestFixture {
    private String userCardNumber = "123456789";
    private String lockerId = "3";

    public OpenLockerRequestFixture withUserCardNumber(String userCardNumber) {
        this.userCardNumber = userCardNumber;
        return this;
    }

    public OpenLockerRequestFixture withLockerId(String lockerId) {
        this.lockerId = lockerId;
        return this;
    }

    public OpenLockerRequest build() {
        OpenLockerRequest openLockerRequest = new OpenLockerRequest();
        openLockerRequest.userCardNumber = userCardNumber;
        openLockerRequest.lockerId = lockerId;
        return openLockerRequest;
    }
}
