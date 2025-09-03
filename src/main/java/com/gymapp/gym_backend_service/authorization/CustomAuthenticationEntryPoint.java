package com.gymapp.gym_backend_service.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymapp.gym_backend_service.data.dto.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String message = "Authentication required or token invalid";

        if(authException instanceof BadCredentialsException) { message = "Invalid username or password"; }
        if(authException instanceof InsufficientAuthenticationException) { message = "Invalid JSON object"; }
//        if(authException instanceof ExpiredJwtException) { message = "Invalid JSON object"; }

        ApiResponse apiResponse = new ApiResponse("Unauthorized", message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}