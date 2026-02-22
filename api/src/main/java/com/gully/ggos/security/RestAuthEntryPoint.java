package com.gully.ggos.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gully.ggos.api.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class RestAuthEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public RestAuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        String requestId = (String) request.getAttribute("requestId");
        ApiErrorResponse body = new ApiErrorResponse(
            false,
            HttpStatus.UNAUTHORIZED.value(),
            "AUTH_EXPIRED",
            "Unauthorized",
            requestId,
            null
        );
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
