package com.personal.site.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<T>(true, data, "OK");
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<T>(false, null, message);
    }
}
