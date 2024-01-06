package ca.ulaval.glo4003.repul.user.domain.identitymanagment.token;

public interface TokenDecoder {
    DecodedToken decode(String token);
}
