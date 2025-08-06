package com.exe.carenest.authorizeservice.infrastructure;

import com.exe.carenest.authorizeservice.authManagement.model.Account;
import com.exe.carenest.authorizeservice.userManagement.service.impl.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    private final RedisService redisService;
    private final long EXPIRATION = 60 * 60 * 1000; // 1 hour

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }



    private boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(
                redisService.get("blacklist:" + token)
        );
    }

    public String generateToken(Account user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("authorities", List.of(user.getRole()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // First check if token is blacklisted
            if (isTokenBlacklisted(token)) {
                return false;
            }
            
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return claimToken(token)
                .getSubject();
    }

    public Claims claimToken(String token){
        return   Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getRole(String token) {
        return claimToken(token)
                .get("role", String.class);
    }
}
