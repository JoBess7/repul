package ca.ulaval.glo4003.repul.fixture.subscription;

import ca.ulaval.glo4003.repul.subscription.api.request.AddCardRequest;

public class AddCardRequestFixture {
    private String cardNumber = "123456789";

    public AddCardRequestFixture withCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public AddCardRequest build() {
        AddCardRequest addCardRequest = new AddCardRequest();
        addCardRequest.cardNumber = cardNumber;

        return addCardRequest;
    }
}
