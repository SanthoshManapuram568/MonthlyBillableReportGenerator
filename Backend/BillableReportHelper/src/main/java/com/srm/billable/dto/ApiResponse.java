package com.srm.billable.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Standard API response wrapper.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;
    private String error;

    public static ApiResponse success(String message) {
        return new ApiResponse(true, message, null);
    }

    public static ApiResponse error(String errorMessage) {
        return new ApiResponse(false, null, errorMessage);
    }
}
