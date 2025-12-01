package com.example.studentadmission.config;

import com.example.studentadmission.filter.AuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration for a stateless API using JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the security filter chain and access rules.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 1. Disable security features unnecessary for stateless REST API
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

        // 2. Set session management to stateless (Crucial for JWT)
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 3. Define Authorization Rules
        http.authorizeHttpRequests(auth -> auth
                // Allow public access to registration, login, and refresh token endpoints
                .requestMatchers("/students/register", "/students/login", "/students/refresh").permitAll()

                // Allow ADMISSION related endpoints (handled inside the filter)
                .requestMatchers("/admissions/**").authenticated()

                // Allow ADMIN access for Institute and Course management
                // Note: Detailed role checking (ADMIN vs SUPER_ADMIN) is best done inside the filter or controller
                .requestMatchers("/institutes/**", "/courses/**").authenticated()

                // Protect all other requests
                .anyRequest().authenticated()
        );

        // 4. Register Custom JWT Authentication Filter
        // We ensure our custom filter runs BEFORE Spring's standard authentication process.
        http.addFilterBefore(new AuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}