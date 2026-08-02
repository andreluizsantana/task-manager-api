package com.project.taskhub.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.taskhub.entity.User;
import java.time.Instant;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenConfiguration {

    @Value("${app.security.jwt.secret}")
    private String secret;

    @Value("${app.security.jwt.expiration}")
    private long expirationTime;

    public String generateToken(User user) {
        Algorithm alg = Algorithm.HMAC256(secret);
        return JWT.create()
                .withClaim("userId", user.getId())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(expirationTime))
                .withIssuedAt(Instant.now())
                .sign(alg);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm alg = Algorithm.HMAC256(secret);
            DecodedJWT decode = JWT.require(alg).build().verify(token);

            return Optional.of(
                    JWTUserData.builder()
                            .userId(decode.getClaim("userId").asLong())
                            .email(decode.getSubject())
                            .build());

        } catch (JWTVerificationException e) {
            log.warn("Invalid token: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
