package com.soha.librarymanagement.errorhandling;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;                 // << Generic payload
    private String error;
    private int statusCode;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // Factory methods
    public static <T> ApiResponse<T> success(T data, String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(statusCode)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return success(data, message, 200);
    }

    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(message)
                .statusCode(statusCode)
                .build();
    }
}
