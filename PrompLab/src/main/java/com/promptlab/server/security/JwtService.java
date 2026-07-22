package com.promptlab.server.security;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration; // Expiration specifically for Access Tokens

    private SecretKey signingKey;
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is missing.");
        }
        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
    }

    // ==========================
    // Access Token Generation (With Expiration)
    // ==========================

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(Collections.emptyMap(), userDetails);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtExpiration)) // Has expiration time
                .signWith(signingKey)
                .compact();
    }

    // ==========================
    // Refresh Token Generation (No JWT Expiration / Opaque Token)
    // ==========================

    public String generateRefreshToken() {
        // Generates a secure random opaque token string to be stored and tracked in your database
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }

    // ==========================
    // Extract Claims
    // ==========================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (JwtException ex) {
            throw new JwtException("Invalid JWT Token", ex);
        }
    }

    // ==========================
    // Validation
    // ==========================

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && userDetails.isEnabled()
                && userDetails.isAccountNonLocked()
                && userDetails.isAccountNonExpired()
                && userDetails.isCredentialsNonExpired()
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}