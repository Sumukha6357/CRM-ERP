package com.gully.ggos.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gully.ggos.api.error.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        String requestId = (String) request.getAttribute("requestId");
        ApiErrorResponse body = new ApiErrorResponse(
            false,
            HttpStatus.FORBIDDEN.value(),
            "FORBIDDEN",
            "Access denied",
            requestId,
            null
        );
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
