package ca.ulaval.glo4003.repul.user.infrastructure.identitymanagement;

import java.util.Date;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.TokenGenerator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTTokenGenerator implements TokenGenerator {
    private static final int EXPIRE_TIME_IN_SECONDS = 3600;
    private final EnvParser envParser = EnvParser.getInstance();
    private final Algorithm algorithm = Algorithm.HMAC256(envParser.readVariable("JWT_SECRET"));

    @Override
    public Token generate(UniqueIdentifier uid, Role role) {
        long sixtyMinutesFromNow = System.currentTimeMillis() + EXPIRE_TIME_IN_SECONDS * 1000;
        Date expireTime = new Date(sixtyMinutesFromNow);

        return new Token(createJWT(uid, role, expireTime), EXPIRE_TIME_IN_SECONDS);
    }

    private String createJWT(UniqueIdentifier uid, Role role, Date expirationTime) {
        return JWT.create().withClaim("UID", uid.getUUID().toString()).withClaim("Role", role.name()).withExpiresAt(expirationTime).sign(algorithm);
    }
}
