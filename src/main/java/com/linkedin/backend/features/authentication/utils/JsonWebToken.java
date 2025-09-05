package com.linkedin.backend.features.authentication.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.repository.httpclient.OauthClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JsonWebToken {
    @Value("${jwt.signerKey}")
    @NonFinal
    protected String SIGNER_KEY;

    RestTemplate restTemplate;






    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SIGNER_KEY.getBytes());
    }


    public String generateToken(User user, TOKEN_TYPE type, Long millis) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(Instant.now().plus(millis, ChronoUnit.MILLIS).toEpochMilli()))
                .signWith(getKey())
                .claim("vanin05", "Hello friend, welcome to linkedin")
                .claim("type", type)
                .compact();
    }
    public String getJti(String token) {
        return extractClaim(token, Claims::getId);
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

    private final String googleCertsUrl = "https://www.googleapis.com/oauth2/v3/certs"; // Google certs endpoint

    public Claims getClaimsFromGoogleOauthIdToken(String idToken) {
        try {
            // Bước 1: Lấy các public keys từ Google (JWK Set)
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    googleCertsUrl, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new AppException("Failed to fetch JWKs from Google.");
            }

            // Bước 2: Lấy các keys từ response
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> keys = (List<Map<String, Object>>) body.get("keys");

            // Bước 3: Lấy kid từ header của ID Token
            String kid = getKidFromIdToken(idToken);

            // Bước 4: Tìm public key tương ứng với kid
            RSAPublicKey rsaPublicKey = findPublicKeyByKid(keys, kid);

            if (rsaPublicKey == null) {
                throw new AppException("No matching public key found for the provided ID token.");
            }

            // Bước 5: Xác thực chữ ký của ID Token với RSA public key
            JwtParser jwtParser = Jwts.parser()
                    .setSigningKey(rsaPublicKey) // Dùng RSA public key để xác thực chữ ký
                    .build();

            // Bước 6: Phân tích ID Token và trả về claims (payload)
            return jwtParser.parseClaimsJws(idToken).getBody();

        } catch (Exception e) {
            throw new AppException("Failed to validate ID token: " + e.getMessage());
        }
    }

    // Lấy kid từ header của ID Token
    private String getKidFromIdToken(String idToken) throws Exception {
        String[] tokenParts = idToken.split("\\."); // Chia JWT thành 3 phần (header, payload, signature)
        String header = new String(Base64.getUrlDecoder().decode(tokenParts[0])); // Giải mã phần header
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> headerMap = objectMapper.readValue(header, Map.class);
        return (String) headerMap.get("kid");
    }

    // Tìm public key tương ứng với kid từ danh sách keys của Google
    private RSAPublicKey findPublicKeyByKid(List<Map<String, Object>> keys, String kid) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Tạo một lần và sử dụng lại

        for (Map<String, Object> key : keys) {
            if (kid.equals(key.get("kid"))) {
                // Lấy modulus (n) và exponent (e) từ key
                BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode((String) key.get("n")));
                BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode((String) key.get("e")));

                RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
                return (RSAPublicKey) keyFactory.generatePublic(rsaPublicKeySpec); // Tạo RSAPublicKey
            }
        }
        return null;
    }
}
