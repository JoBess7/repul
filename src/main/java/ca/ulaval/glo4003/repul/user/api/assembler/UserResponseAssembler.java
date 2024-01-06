package ca.ulaval.glo4003.repul.user.api.assembler;

import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;

public class UserResponseAssembler {
    public LoginResponse toLoginResponse(Token token) {
        return new LoginResponse(token.value(), token.expiresIn());
    }
}
