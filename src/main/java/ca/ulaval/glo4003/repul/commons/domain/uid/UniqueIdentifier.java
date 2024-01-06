package ca.ulaval.glo4003.repul.commons.domain.uid;

import java.util.UUID;

public class UniqueIdentifier {
    private final UUID value;

    protected UniqueIdentifier(UUID value) {
        this.value = value;
    }

    public UUID getUUID() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof UniqueIdentifier)) {
            return false;
        }
        UniqueIdentifier otherUniqueIdentifier = (UniqueIdentifier) other;
        return otherUniqueIdentifier.getUUID().equals(this.getUUID());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
