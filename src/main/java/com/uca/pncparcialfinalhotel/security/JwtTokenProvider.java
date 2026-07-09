package com.uca.pncparcialfinalhotel.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.pncparcialfinalhotel.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final String secret;
    private final long accessExpirationMs;
    private final ObjectMapper objectMapper;

    public JwtTokenProvider(
            @Value("${app.jwt-secret}") String secret,
            @Value("${app.jwt-access-expiration-ms}") long accessExpirationMs,
            ObjectMapper objectMapper
    ) {
        this.secret = secret;
        this.accessExpirationMs = accessExpirationMs;
        this.objectMapper = objectMapper;
    }

    public String generateAccessToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return generateAccessToken(user);
    }

    public String generateAccessToken(User user) {
        try {
            Instant now = Instant.now();
            Instant expiry = now.plusMillis(accessExpirationMs);

            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", user.getEmail());
            payload.put("userId", user.getId());
            payload.put("role", user.getRole().name());
            payload.put("iat", now.getEpochSecond());
            payload.put("exp", expiry.getEpochSecond());
            payload.put("type", "ACCESS");

            String encodedHeader = encode(objectMapper.writeValueAsBytes(header));
            String encodedPayload = encode(objectMapper.writeValueAsBytes(payload));
            String content = encodedHeader + "." + encodedPayload;
            String signature = sign(content);

            return content + "." + signature;
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate JWT.", ex);
        }
    }

    public String getUsernameFromToken(String token) {
        Map<String, Object> payload = getPayload(token);
        return String.valueOf(payload.get("sub"));
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String content = parts[0] + "." + parts[1];
            String expectedSignature = sign(content);

            if (!constantTimeEquals(expectedSignature, parts[2])) {
                return false;
            }

            Map<String, Object> payload = getPayload(token);
            Object type = payload.get("type");
            Object exp = payload.get("exp");

            if (!"ACCESS".equals(String.valueOf(type))) {
                return false;
            }

            long expirationSeconds = Long.parseLong(String.valueOf(exp));
            return Instant.now().getEpochSecond() < expirationSeconds;
        } catch (Exception ex) {
            return false;
        }
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    private Map<String, Object> getPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            byte[] decodedPayload = BASE64_URL_DECODER.decode(parts[1]);
            return objectMapper.readValue(decodedPayload, new TypeReference<>() {});
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid JWT payload.", ex);
        }
    }

    private String encode(byte[] bytes) {
        return BASE64_URL_ENCODER.encodeToString(bytes);
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(keySpec);
            return encode(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not sign JWT.", ex);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        if (aBytes.length != bBytes.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }
}
