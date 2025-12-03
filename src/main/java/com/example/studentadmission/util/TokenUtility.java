package com.example.studentadmission.util;

import com.example.studentadmission.entity.Student;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TokenUtility {

    // Using a strong, static key for simplicity in this example
    // In a real application, this should be loaded from application.properties securely
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long ACCESS_TOKEN_EXPIRATION_MS = 1000 * 60 * 60; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 1000 * 60 * 60 * 24 * 7; // 7 days

    // --- Core Methods ---

    public static String generateAccessToken(Student student) {
        return createToken(student, ACCESS_TOKEN_EXPIRATION_MS);
    }

    public static String generateRefreshToken(Student student) {
        return createToken(student, REFRESH_TOKEN_EXPIRATION_MS);
    }

    private static String createToken(Student student, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();

        // --- FIX: Using getId() instead of getStudentId() ---
        claims.put("id", student.getStudentId());
        claims.put("role", student.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(student.getStudentEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Validation and Extraction Methods ---

    public static Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            // Log the exception (e.g., SignatureException, MalformedJwtException)
            return false;
        }
    }

    public static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Map<String, Object> getClaims(String token) {
        return extractAllClaims(token);
    }
}