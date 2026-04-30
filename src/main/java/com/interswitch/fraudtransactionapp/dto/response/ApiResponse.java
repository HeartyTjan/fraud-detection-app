package com.interswitch.fraudtransactionapp.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ApiResponse<T>{
    private String status;
    private String message;
    private T data;

    public static <T> ApiResponse <T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("Success")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse <T> success(String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .build();
    }

    public static <T> ApiResponse <T> error(String message) {
        return ApiResponse.<T>builder()
                .status("failed")
                .message(message)
                .build();
    }
}
