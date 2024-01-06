package ca.ulaval.glo4003.repul.small.user.domain.identitymanagement;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.user.domain.identitymanagment.exception.InvalidTokenException;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenTest {
    private static final String AN_EMPTY_TOKEN = "";
    private static final String A_TOKEN = "aToken";
    private static final int AN_EXPIRATION_TIME = 3600;
    private static final int A_NEGATIVE_EXPIRATION_TIME = -1;

    @Test
    public void givenEmptyValue_whenCreatingToken_shouldThrowInvalidTokenException() {
        assertThrows(InvalidTokenException.class, () -> new Token(AN_EMPTY_TOKEN, AN_EXPIRATION_TIME));
    }

    @Test
    public void givenNegativeExpirationTime_whenCreatingToken_shouldThrowInvalidTokenException() {
        assertThrows(InvalidTokenException.class, () -> new Token(A_TOKEN, A_NEGATIVE_EXPIRATION_TIME));
    }
}
