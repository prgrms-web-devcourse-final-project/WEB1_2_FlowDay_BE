package org.example.flowday.global.security.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public Map<String,Object> getClaims(String token) {

        return (Map<String, Object>) Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("data");
    }

    public String getUsername(String token) {
        return (String) getClaims(token).get("loginId");
    }

    public Integer getId(String token) {
        return (Integer) getClaims(token).get("id");
    }

    public String getRole(String token) {
        return (String) getClaims(token).get("role");
    }

    public String getCategory(String token) {
        return (String) getClaims(token).get("category");
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public Date getExpiration(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
    }

    public String createJwt(Map<String, Object> data, Long expiredMs) {
        Claims claims = Jwts
                .claims()
                .add("data", data)
                .add("type", "access_token")
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }
}
