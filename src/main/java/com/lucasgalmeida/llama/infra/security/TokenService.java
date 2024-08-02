package com.lucasgalmeida.llama.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lucasgalmeida.llama.domain.entities.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Slf4j
public class TokenService {

    private static final String ISSUER = "login-auth-api";
    @Value("${api.security.token.secret}")
    private String secret;
    @Value("${api.security.token.expiration-hours:2}")
    private int expirationHours;

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getLogin())
                    .withExpiresAt(this.gerarDataDeExpiracao())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            log.error("Error on token generation for user: {}", user.getLogin(), exception);
            throw new JWTCreationException("Erro ao gerar o token", exception);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            log.error("Error on token validation", exception);
            return null;
        }
    }

    private Instant gerarDataDeExpiracao() {
        return LocalDateTime.now().plusHours(expirationHours).toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
    }
}
