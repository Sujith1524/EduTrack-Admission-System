package com.example.studentadmission.util;

import com.example.studentadmission.entity.Student;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class TokenUtility {

    // Secret Key (Keep this safe! In real apps, store in application.properties)
    private static final String SECRET_KEY = "MySuperSecretKeyForStudentAdmissionApp123!";
    private static final long EXPIRATION_TIME_MS = 1000 * 60 * 60 * 2; // 2 Hours

    // --- 1. GENERATE TOKEN ---
    public static String generateToken(Student student) {
        try {
            long nowMillis = System.currentTimeMillis();
            long expMillis = nowMillis + EXPIRATION_TIME_MS;

            // Simple Payload: {"sub":"email", "id":1, "exp":123456789}
            String payload = String.format(
                    "{\"sub\":\"%s\",\"id\":%d,\"exp\":%d}",
                    student.getEmail(),
                    student.getStudentId(),
                    expMillis
            );

            // Encode Payload
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

            // Create Signature
            String signature = sign(encodedPayload);

            // Final Token: payload.signature
            return encodedPayload + "." + signature;

        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    // --- 2. VALIDATE TOKEN ---
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 2) return false;

            String payload = parts[0];
            String signature = parts[1];

            // 1. Verify Signature
            String expectedSignature = sign(payload);
            if (!expectedSignature.equals(signature)) return false;

            // 2. Verify Expiration
            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload));
            // Extract "exp":123456... part
            long expTimestamp = Long.parseLong(decodedPayload.substring(
                    decodedPayload.lastIndexOf(":") + 1,
                    decodedPayload.lastIndexOf("}")));

            return System.currentTimeMillis() < expTimestamp;

        } catch (Exception e) {
            return false;
        }
    }

    // Helper: HMAC SHA256 Signature
    private static String sign(String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}
