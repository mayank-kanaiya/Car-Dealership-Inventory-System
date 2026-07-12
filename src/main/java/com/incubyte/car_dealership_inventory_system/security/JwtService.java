package com.incubyte.car_dealership_inventory_system.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class JwtService {

    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final byte[] secretKeyBytes;
    private final long expirationMillis;
    private final Clock clock;

    public JwtService(String secretKey, long expirationMillis) {
        this(secretKey, expirationMillis, Clock.systemUTC());
    }

    JwtService(String secretKey, long expirationMillis, Clock clock) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("Secret key must not be blank");
        }
        if (expirationMillis <= 0) {
            throw new IllegalArgumentException("Expiration must be greater than zero");
        }

        this.secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.expirationMillis = expirationMillis;
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public String generateToken(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject must not be blank");
        }

        long issuedAt = clock.millis();
        long expiresAt = issuedAt + expirationMillis;

        String payloadJson = "{\"sub\":\"" + escapeJson(subject) + "\","
            + "\"iat\":" + issuedAt + ","
            + "\"exp\":" + expiresAt + "}";

        String encodedHeader = encode(HEADER_JSON.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = encode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = encodedHeader + "." + encodedPayload;
        String signature = sign(signingInput);

        return signingInput + "." + signature;
    }

    public String extractEmail(String token) {
        JwtClaims claims = parseAndValidate(token, false);
        return claims.subject();
    }

    public boolean isValidToken(String token, String expectedSubject) {
        try {
            JwtClaims claims = parseAndValidate(token, true);
            return claims.subject().equals(expectedSubject);
        } catch (ExpiredTokenException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private JwtClaims parseAndValidate(String token, boolean rejectExpired) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token must not be blank");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Token is not a valid JWT");
        }

        String signingInput = parts[0] + "." + parts[1];
        String expectedSignature = sign(signingInput);
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            throw new IllegalArgumentException("Token signature is invalid");
        }

        String payloadJson = new String(BASE64_URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
        JwtClaims claims = parseClaims(payloadJson);

        if (rejectExpired && claims.expirationMillis() < clock.millis()) {
            throw new ExpiredTokenException("Token has expired");
        }

        return claims;
    }

    private JwtClaims parseClaims(String payloadJson) {
        String subject = extractStringClaim(payloadJson, "sub");
        long expiration = extractLongClaim(payloadJson, "exp");
        return new JwtClaims(subject, expiration);
    }

    private String extractStringClaim(String json, String claimName) {
        String pattern = "\"" + claimName + "\":\"";
        int start = json.indexOf(pattern);
        if (start < 0) {
            throw new IllegalArgumentException("Missing claim: " + claimName);
        }

        start += pattern.length();
        int end = json.indexOf('"', start);
        if (end < 0) {
            throw new IllegalArgumentException("Invalid claim: " + claimName);
        }

        return unescapeJson(json.substring(start, end));
    }

    private long extractLongClaim(String json, String claimName) {
        String pattern = "\"" + claimName + "\":";
        int start = json.indexOf(pattern);
        if (start < 0) {
            throw new IllegalArgumentException("Missing claim: " + claimName);
        }

        start += pattern.length();
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }

        if (end == start) {
            throw new IllegalArgumentException("Invalid claim: " + claimName);
        }

        return Long.parseLong(json.substring(start, end));
    }

    private String sign(String signingInput) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKeyBytes, "HmacSHA256"));
            return encode(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("HmacSHA256 is not available", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign token", ex);
        }
    }

    private String encode(byte[] value) {
        return BASE64_URL_ENCODER.encodeToString(value);
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigest.isEqual(
            left.getBytes(StandardCharsets.UTF_8),
            right.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String escapeJson(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }

    private String unescapeJson(String value) {
        return value
            .replace("\\\"", "\"")
            .replace("\\\\", "\\");
    }

    private record JwtClaims(String subject, long expirationMillis) {
    }
}