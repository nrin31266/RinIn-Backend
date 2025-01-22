package com.linkedin.backend.features.authentication.utils;


import com.linkedin.backend.features.authentication.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JsonWebToken {
    @Value("${jwt.signerKey}")
    @NonFinal
    protected String SIGNER_KEY;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SIGNER_KEY.getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .signWith(getKey())
                .claim("vanin05", "Hello friend, welcome to linkedin")
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }


    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String getEmailFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }
}
