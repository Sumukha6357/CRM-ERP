package com.gully.ggos.api.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean ok;
    private int status;
    private String code;
    private String message;
    private String requestId;
    private Object details;
}
