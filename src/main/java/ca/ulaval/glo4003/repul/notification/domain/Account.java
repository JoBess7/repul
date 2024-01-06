package ca.ulaval.glo4003.repul.notification.domain;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public abstract class Account {
    protected final UniqueIdentifier accountId;
    protected final Email email;

    public Account(UniqueIdentifier accountId, Email email) {
        this.accountId = accountId;
        this.email = email;
    }

    public UniqueIdentifier getAccountId() {
        return accountId;
    }

    public Email getEmail() {
        return email;
    }
}
