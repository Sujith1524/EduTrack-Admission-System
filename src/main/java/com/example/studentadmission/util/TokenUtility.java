package com.example.studentadmission.util;

import com.example.studentadmission.entity.Student;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TokenUtility {

    // Simple fixed secret key for demonstration purposes
    private static final String SECRET_KEY = "YourSecureAndLongJWTSecretKeyForSigning";
    private static final long EXPIRATION_TIME_MS = TimeUnit.HOURS.toMillis(2); // Token valid for 2 hours

    /**
     * Simulates generating a JWT-like token string for a student.
     * The token contains a payload with student ID, creation time, and expiry time.
     */
    public static String generateToken(Student student) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + EXPIRATION_TIME_MS);

        // Header (simulated)
        String header = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());

        // Payload (Contains user info and expiry)
        String payload = String.format(
                "{\"sub\":\"%s\",\"id\":%d,\"iat\":%d,\"exp\":%d}",
                student.getEmail(),
                student.getStudentId(),
                now.getTime() / 1000,
                expiration.getTime() / 1000
        );
        String encodedPayload = Base64.getEncoder().encodeToString(payload.getBytes());

        // Signature (Simulated signature - not secure)
        String signature = Base64.getEncoder().encodeToString((header + "." + encodedPayload + SECRET_KEY).getBytes());

        return header + "." + encodedPayload + "." + signature;
    }
}
