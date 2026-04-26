package com.project.taskhub.security;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.taskhub.entity.User;

@Component
public class TokenConfiguration {

    private String secret = "tasktask";

    public String generatorToken(User user) {
	Algorithm alg = Algorithm.HMAC256(secret);
	return JWT.create().withClaim("userId", user.getId()).withSubject(user.getEmail()).withExpiresAt(Instant.now().plusSeconds(86400)).withIssuedAt(Instant.now()).sign(alg);
    }

    public Optional<JWTUserData> validateToken(String token) {
	try {
	    Algorithm alg = Algorithm.HMAC256(secret);
	    DecodedJWT decode = JWT.require(alg).build().verify(token);
	    return Optional.of(JWTUserData.builder().userId(decode.getClaim("userId").asLong()).email(decode.getSubject()).build());
	} catch (JWTVerificationException e) {
	    return Optional.empty();
	}
    }
}
