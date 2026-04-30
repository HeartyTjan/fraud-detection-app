package com.interswitch.fraudtransactionapp.config;


import com.interswitch.fraudtransactionapp.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class JwtUtil {
    private final long EXPIRATION;
    private final Key key;


    public JwtUtil(
            @Value("${jwt_secret}") String secret,
            @Value("${jwt_expiration}") String expiration) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is not configured");
        }

        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 256 bits (32+ characters)");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.EXPIRATION = Long.parseLong(expiration);
    }

    public String generateToken(String ownerName) {

        Role role = Role.ADMIN;
        List<String> permissions = role.getPermissions()
                .stream()
                .map(Enum::name)
                .toList();

        return Jwts.builder()
                .setSubject(ownerName)
                .claim("role", role.name())
                .claim("permissions", permissions)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public List<String> extractPermissions(String token) {
        Claims claims = extractAllClaims(token);
        List<?> rawList = claims.get("permissions", List.class);
        return rawList.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}