package com.capfer.hotel.booking.exceptions;

import com.capfer.hotel.booking.dtos.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handle:
 * 401 Unauthorized means authentication is required and has failed or not been provided,
 * while 403 Forbidden means the server understands the request but refuses to authorize it.
 * In short: 401 is "I don't know who you are," and 403 is "I know who you are, but you cannot do this.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseDTO responseDTO = ResponseDTO.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value()) // 401 invalid token or no token
                .message("Unauthorized: " + authException.getMessage())
                .build();

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(responseDTO));
    }
}
