package com.gully.ggos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final String issuer;

    public JwtService(
        @Value("${security.jwt.secret}") String secret,
        @Value("${security.jwt.issuer}") String issuer
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
    }

    public String generateToken(
        UUID userId,
        UUID orgId,
        String email,
        Set<String> roles,
        Set<String> permissions
    ) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuer(issuer)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(8 * 3600)))
            .addClaims(Map.of(
                "orgId", orgId.toString(),
                "email", email,
                "roles", roles,
                "perms", permissions
            ))
            .signWith(key)
            .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .requireIssuer(issuer)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
