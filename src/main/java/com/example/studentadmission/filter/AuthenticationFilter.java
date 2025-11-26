package com.example.studentadmission.filter;

import com.example.studentadmission.entity.Student;
import com.example.studentadmission.util.TokenUtility;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        // 1. PUBLIC ENDPOINTS (No token required)
        if (path.startsWith("/students/login") ||
                path.startsWith("/students/register") ||
                path.startsWith("/students/refresh")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. TOKEN EXTRACTION & PRESENCE CHECK
        String authHeader = httpRequest.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            sendError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Missing Token");
            return;
        }

        // 3. CHECK TOKEN VALIDITY (Signature check)
        if (!TokenUtility.validateToken(token)) {
            sendError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid Token Signature");
            return;
        }

        // 4. CHECK TOKEN EXPIRATION (Moved up to give priority to expiration error)
        if (TokenUtility.isTokenExpired(token)) {
            sendError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Access Token Expired. Use Refresh Token to get a new one.");
            return;
        }

        // 5. EXTRACT CLAIMS (Done after basic validation and expiration check)
        Map<String, Object> claims = TokenUtility.getClaims(token);

        // Ensure claims were successfully parsed
        if (claims == null || claims.isEmpty()) {
            sendError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Token claims could not be read.");
            return;
        }

        String tokenType = (String) claims.get("type");
        Student.Role userRole = (Student.Role) claims.get("role");

        // 6. CRITICAL FIX: CHECK TOKEN TYPE (Refresh tokens cannot access APIs)
        if (!"access".equals(tokenType)) {
            sendError(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Forbidden: This token is a Refresh Token and cannot be used for direct API access.");
            return;
        }

        // 7. ROLE-BASED ACCESS CONTROL (RBAC)
        if (path.startsWith("/institutes") ||
                path.startsWith("/courses") ||
                (path.startsWith("/admissions") && !path.startsWith("/api/admissions/student"))) {

            if (userRole != Student.Role.ADMIN && userRole != Student.Role.SUPER_ADMIN) {
                sendError(httpResponse, HttpServletResponse.SC_FORBIDDEN, "Forbidden: Insufficient privileges (ADMIN or SUPER_ADMIN required).");
                return;
            }
        }

        // 8. SUCCESS: Allow request to proceed
        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse httpResponse, int status, String message) throws IOException {
        httpResponse.setStatus(status);
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write(String.format("{\"status\": \"Error\", \"message\": \"%s\"}", message));
    }
}