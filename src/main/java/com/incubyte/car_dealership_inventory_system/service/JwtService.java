package com.incubyte.car_dealership_inventory_system.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.incubyte.car_dealership_inventory_system.exception.ExpiredTokenException;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Handles JWT token lifecycle — generation, claim extraction, and validation —
 * using HMAC-SHA256 signing. The signing key is derived at runtime from a
 * Base64-encoded secret configured via {@code application.security.jwt.secret-key}.
 */
@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class JwtService {

    @Value("${application.security.jwt.secret-key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${application.security.jwt.expiration:3600000}")
    private long expirationMillis;

    /**
     * Generates a signed JWT with the given email as the subject claim.
     *
     * @param email the user's email, embedded as the {@code sub} claim
     * @return a compact, signed JWT string
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the email (subject claim) from a JWT.
     *
     * @param token the compact JWT string
     * @return the email stored in the {@code sub} claim
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates that a token is authentic, not expired, and was issued for
     * the expected user.
     *
     * @param token         the compact JWT string
     * @param expectedEmail the email the token must belong to
     * @return {@code true} if the token is valid and matches the expected email
     * @throws ExpiredTokenException if the token is expired (propagated so callers
     *                               can distinguish expiry from general tampering)
     */
    public boolean isValidToken(String token, String expectedEmail) {
        try {
            final String userEmail = extractEmail(token);
            return (userEmail.equals(expectedEmail)) && !isTokenExpired(token);
        } catch (ExpiredTokenException e) {
            throw e;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException("Token has expired");
        }
    }

    /**
     * Derives an HMAC-SHA key from the Base64-encoded secret property.
     * The raw secret string is first decoded from Base64 into bytes, then
     * passed to {@link Keys#hmacShaKeyFor} which selects the appropriate
     * HMAC key size for the HS256 algorithm (256-bit minimum).
     *
     * @return the signing key used for JWT signature creation and verification
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
