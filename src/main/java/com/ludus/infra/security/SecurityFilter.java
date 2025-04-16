package com.ludus.infra.security;

import com.ludus.services.TokenService;
import com.ludus.services.UserService;
import com.ludus.exceptions.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    UserService userService;
    private TokenService tokenService;

    SecurityFilter(@Lazy TokenService tokenService, UserService userService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null) {
            try {
                var login = tokenService.validateToken(token);
                UserDetails userDetails = userService.loadUserByEmail(login);
                if (userDetails == null) {
                    throw new NotFoundException("User not found");
                }
                System.out.println("User's roles : " + userDetails.getAuthorities());

                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                System.err.println("Error during token validation: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
