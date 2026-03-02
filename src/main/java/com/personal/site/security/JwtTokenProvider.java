package com.personal.site.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expiryMs;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expire-ms}") long expiryMs) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiryMs = expiryMs;
    }

    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiryMs);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserId(String token) {
        Claims claims = parse(token);
        return Long.valueOf(claims.getSubject());
    }

    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
