package com.ludus.infra.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.web.AuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Acess denied. You need to log in to access this resource.");

        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(jsonResponse);
    }

}
