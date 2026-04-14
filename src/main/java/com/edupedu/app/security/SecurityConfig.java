package com.edupedu.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                   .authorizeHttpRequests(auth -> auth
                                                      .requestMatchers("/api/v1/auth/**", "/api/v1/register").permitAll()
                                                      .requestMatchers(PUBLIC_URLS).permitAll()
                                                      
                                                      // Admin only
                                                      .requestMatchers("/api/v1/universities/**").hasRole("ADMIN")
                                                      .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                                                      .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                                      
                                                      // Admin and University Admin
                                                      .requestMatchers("/api/v1/faculties/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN")
                                                      .requestMatchers("/api/v1/subjects/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN")
                                                      .requestMatchers("/api/v1/teachers/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN")
                                                      .requestMatchers("/api/v1/students/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN")
                                                      .requestMatchers("/api/v1/student-groups/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN")
                                                      
                                                      // Teachers, Students, and Admins
                                                      .requestMatchers("/api/v1/courses/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER", "STUDENT")
                                                      .requestMatchers("/api/v1/announcements/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER", "STUDENT")
                                                      .requestMatchers("/api/v1/attendance/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER", "STUDENT")
                                                      .requestMatchers("/api/v1/grades/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER", "STUDENT")
                                                      .requestMatchers("/api/v1/schedule/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER", "STUDENT")
                                                      .requestMatchers("/api/v1/tests/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER", "STUDENT")
                                                      .requestMatchers("/api/v1/enrollments/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER", "STUDENT")
                                                      .requestMatchers("/api/v1/progress/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER", "STUDENT")
                                                      .requestMatchers("/api/v1/messages/**").hasAnyRole("ADMIN", "UNIVERSITY_ADMIN", "TEACHER", "STUDENT")

                                                      .anyRequest().authenticated())
                   .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                   .authenticationProvider(this.authenticationProvider)
                   .addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class)
                   .build();
    }


}
