package com.example.studentadmission.filter;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.util.TokenUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Paths that do not require authentication (allow access)
    private static final String[] PUBLIC_PATHS = {
            "/students/register",
            "/students/login",
            "/students/refresh"
    };

    // Paths that require ADMIN role (Institute, Course management)
    private static final String[] ADMIN_PATHS = {
            "/institutes/add",
            "/institutes/update",
            "/courses/add",
            "/courses/update"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // --- 1. Handle Public Paths ---
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // --- 2. Extract Token from Header ---
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, "Authorization header missing or invalid.", HttpStatus.UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        // --- 3. Validate Token ---
        if (!TokenUtility.validateToken(token)) {
            sendErrorResponse(response, "Invalid or expired token.", HttpStatus.UNAUTHORIZED);
            return;
        }

        try {
            Claims claims = TokenUtility.extractAllClaims(token);

            // Extract role (comes as String from JWT)
            String roleString = claims.get("role", String.class);

            // LINE 64 FIX: Convert String to Enum using valueOf()
            Student.Role role = Student.Role.valueOf(roleString);

            // Extract user ID (comes as Integer/Long, cast to Long)
            Long studentId = ((Number) claims.get("id")).longValue();

            // --- 4. Role-Based Authorization Check ---

            // Check if the request is trying to access an Admin-only resource
            if (isAdminPath(path)) {
                if (role != Student.Role.ADMIN && role != Student.Role.SUPER_ADMIN) {
                    sendErrorResponse(response, "Access Denied: Requires ADMIN privileges.", HttpStatus.FORBIDDEN);
                    return;
                }
            }

            // --- 5. Set Authentication Context ---

            // Create the list of authorities (roles) for Spring Security
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());

            // Create the authentication object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            studentId, // Principal: The student's ID (or email/username)
                            null,      // Credentials: null (already validated by token)
                            Collections.singletonList(authority) // Authorities/Roles
                    );

            // Store the authenticated user in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (IllegalArgumentException e) {
            // Catches errors if valueOf(roleString) fails (e.g., malformed role in token)
            sendErrorResponse(response, "Invalid role contained in token.", HttpStatus.UNAUTHORIZED);
            return;
        } catch (Exception e) {
            // Catch JWT extraction/parsing issues
            sendErrorResponse(response, "Token processing failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            return;
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    private boolean isAdminPath(String path) {
        for (String adminPath : ADMIN_PATHS) {
            if (path.startsWith(adminPath)) {
                return true;
            }
        }
        return false;
    }

    private void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");

        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);
        errorDetails.put("timestamp", new java.util.Date().toString());

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}