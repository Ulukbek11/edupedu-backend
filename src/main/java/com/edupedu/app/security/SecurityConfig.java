package com.edupedu.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
            "/api/v1/register",
            "/api/v1/auth/**",
            "/api/v1/forgot-password",
            "/api/v1/reset-password",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/ws-chat",
            "/ws-chat/**",
            "/",
            "/index.html",
    };

    private final JwtFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                   .csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(auth -> auth
                                                      .requestMatchers("/api/v1/auth/**").permitAll()
                                                      .requestMatchers(PUBLIC_URLS).permitAll()
                                                      
                                                      // Admin and University Admin only (CRUD operations)
                                                      .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN")
                                                      
                                                      // Teacher-level access (teachers + admins)
                                                      .requestMatchers("/api/v1/teacher/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER")
                                                      
                                                      // University-scoped reads (admin, uni admin, teachers)
                                                      .requestMatchers("/api/v1/university/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER")
                                                      
                                                      // All authenticated users
                                                      .anyRequest().authenticated())
                   .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   .authenticationProvider(this.authenticationProvider)
                   .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class)
                   .build();
    }


}
