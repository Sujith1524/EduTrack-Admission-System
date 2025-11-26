package com.example.studentadmission.filter;

import com.example.studentadmission.util.TokenUtility;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. Allow public endpoints (Login/Register/Swagger) without token
        String path = httpRequest.getRequestURI();
        if (path.startsWith("/students/login") || path.startsWith("/students/register")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Get Authorization Header
        String authHeader = httpRequest.getHeader("Authorization");

        // 3. Check format "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer "

            // 4. Validate Token
            if (TokenUtility.validateToken(token)) {
                // Token is valid, allow request to proceed
                chain.doFilter(request, response);
                return;
            }
        }

        // 5. If invalid or missing, return 401 Unauthorized
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.setContentType("application/json");
        httpResponse.getWriter().write("{\"status\": \"Error\", \"message\": \"Unauthorized: Invalid or Missing Token\"}");
    }
}