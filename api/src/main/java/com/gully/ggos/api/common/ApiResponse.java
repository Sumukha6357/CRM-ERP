package com.gully.ggos.api.common;

import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private T data;
    private ApiMeta meta;
    private ApiError error;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .data(data)
            .meta(ApiMeta.builder().timestamp(OffsetDateTime.now()).build())
            .build();
    }

    public static <T> ApiResponse<T> success(T data, ApiMeta meta) {
        return ApiResponse.<T>builder()
            .data(data)
            .meta(meta)
            .build();
    }

    public static ApiResponse<Void> error(ApiError error) {
        return ApiResponse.<Void>builder()
            .error(error)
            .meta(ApiMeta.builder().timestamp(OffsetDateTime.now()).build())
            .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ApiMeta {
        private OffsetDateTime timestamp;
        private Long totalElements;
        private Integer totalPages;
        private Integer size;
        private Integer number;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ApiError {
        private int status;
        private String code;
        private String message;
        private String requestId;
        private Object details;
    }
}
