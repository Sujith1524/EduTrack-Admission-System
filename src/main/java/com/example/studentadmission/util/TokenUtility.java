package com.example.studentadmission.util;

import com.example.studentadmission.entity.Student;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenUtility {

    private static final String SECRET_KEY = "MySuperSecretKeyForStudentAdmissionApp123!_EnhancedSecurity";

    // JWT Configuration
    private static final long ACCESS_EXPIRATION_MS = 1000 * 60 * 30; // 30 Minutes
    private static final long REFRESH_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7; // 7 Days

    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\":(\\d+)");
    private static final Pattern ID_PATTERN = Pattern.compile("\"id\":(\\d+)");
    private static final Pattern ROLE_PATTERN = Pattern.compile("\"role\":\"([A-Z_]+)\"");
    private static final Pattern TYPE_PATTERN = Pattern.compile("\"type\":\"([a-z]+)\""); // New Pattern

    // Helper: HMAC SHA256 Signature
    private static String sign(String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    // --- 1. GENERATE ACCESS TOKEN ---
    public static String generateAccessToken(Student student) {
        return generateToken(student, ACCESS_EXPIRATION_MS, "access"); // Explicitly set type: access
    }

    // --- 2. GENERATE REFRESH TOKEN ---
    public static String generateRefreshToken(Student student) {
        return generateToken(student, REFRESH_EXPIRATION_MS, "refresh"); // Explicitly set type: refresh
    }

    private static String generateToken(Student student, long expirationMs, String tokenType) {
        try {
            long nowMillis = System.currentTimeMillis();
            long expMillis = nowMillis + expirationMs;

            // Include ID, Email, Role, Type, and Expiration
            String payload = String.format(
                    "{\"sub\":\"%s\",\"id\":%d,\"role\":\"%s\",\"type\":\"%s\",\"exp\":%d}",
                    student.getEmail(),
                    student.getStudentId(),
                    student.getRole().toString(),
                    tokenType, // New field in payload
                    expMillis
            );

            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

            String signature = sign(encodedPayload);

            return encodedPayload + "." + signature;

        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    // --- 3. VALIDATE TOKEN (Signature check only) ---
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 2) return false;

            String payload = parts[0];
            String signature = parts[1];

            String expectedSignature = sign(payload);
            return expectedSignature.equals(signature);

        } catch (Exception e) {
            return false;
        }
    }

    // --- 4. CHECK EXPIRATION ---
    public static boolean isTokenExpired(String token) {
        try {
            String payload = token.split("\\.")[0];
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));

            Matcher matcher = EXP_PATTERN.matcher(decodedPayload);
            if (matcher.find()) {
                long expTimestamp = Long.parseLong(matcher.group(1));
                return System.currentTimeMillis() > expTimestamp;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    // --- 5. EXTRACT CLAIMS (ID, Role, and Type) ---
    public static Map<String, Object> getClaims(String token) {
        try {
            String payload = token.split("\\.")[0];
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));

            Map<String, Object> claims = new HashMap<>();

            // Extract ID
            Matcher idMatcher = ID_PATTERN.matcher(decodedPayload);
            if (idMatcher.find()) claims.put("id", Long.parseLong(idMatcher.group(1)));

            // Extract Role
            Matcher roleMatcher = ROLE_PATTERN.matcher(decodedPayload);
            if (roleMatcher.find()) claims.put("role", Student.Role.valueOf(roleMatcher.group(1)));

            // Extract Type (NEW)
            Matcher typeMatcher = TYPE_PATTERN.matcher(decodedPayload);
            if (typeMatcher.find()) claims.put("type", typeMatcher.group(1));

            return claims;
        } catch (Exception e) {
            return Map.of();
        }
    }
}
