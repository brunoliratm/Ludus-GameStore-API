package com.ludus.infra.security;

import com.ludus.handlers.CustomAccessDeniedHandler;
import com.ludus.handlers.CustomAuthenticationEntryHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private final SecurityFilter securityFilter;

    public SecurityConfiguration(SecurityFilter securityFilter) {
        this.securityFilter = securityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
            CustomAuthenticationEntryHandler customAuthenticationEntryHandler) throws Exception {

        return httpSecurity.csrf(csrf -> csrf.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/v1/purchases/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/games/**").permitAll()
                        .requestMatchers("/api/v1/games/**").hasRole("ADMIN")
                        
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/api-docs").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryHandler)
                        .accessDeniedHandler(new CustomAccessDeniedHandler()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
