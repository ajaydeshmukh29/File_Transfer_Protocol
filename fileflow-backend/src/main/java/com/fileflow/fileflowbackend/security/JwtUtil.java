package com.fileflow.fileflowbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Small wrapper around JJWT for generating and validating access tokens.
 * The secret and expiry come from application.properties (jwt.secret /
 * jwt.expiration-ms) so they're easy to swap per-environment.
 */
@Component
public class JwtUtil
{
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey()
    {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email)
    {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(email)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractEmail(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String email)
    {
        try
        {
            String tokenEmail = extractEmail(token);

            return tokenEmail.equals(email) && !isTokenExpired(token);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private boolean isTokenExpired(String token)
    {
        Date expiration = extractClaim(token, Claims::getExpiration);

        return expiration.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver)
    {
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return resolver.apply(claims);
    }
}
