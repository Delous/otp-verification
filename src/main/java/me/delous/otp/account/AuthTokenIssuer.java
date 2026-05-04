package me.delous.otp.account;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import me.delous.otp.account.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenIssuer {
    private final Clock clock;
    private final long ttlMinutes;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public AuthTokenIssuer(
            Clock clock,
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.ttl-minutes}") long ttlMinutes
    ) {
        this.clock = clock;
        this.ttlMinutes = ttlMinutes;
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer("otp-verification").build();
    }

    public TokenResponse issueToken(Account account) {
        Instant now = clock.instant();
        Instant expiresAt = now.plus(ttlMinutes, ChronoUnit.MINUTES);
        String token = JWT.create()
                .withIssuer("otp-verification")
                .withSubject(String.valueOf(account.accountId()))
                .withClaim("username", account.username())
                .withClaim("role", account.accountRole().name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm);
        return new TokenResponse("Bearer", token, expiresAt);
    }

    public DecodedJWT verify(String token) {
        return verifier.verify(token);
    }
}
