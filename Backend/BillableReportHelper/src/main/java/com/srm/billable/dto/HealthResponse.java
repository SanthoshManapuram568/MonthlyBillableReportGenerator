package com.srm.billable.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Health check response.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

    private String status;  // "UP"
    private String version; // "1.0.0"

    public static HealthResponse up() {
        return new HealthResponse("UP", "1.0.0");
    }
}
