package ca.ulaval.glo4003.repul.user.api.response;

public record LoginResponse(
    String token,
    int expiresIn
) {
}
