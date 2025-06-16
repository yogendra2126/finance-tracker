package com.yogendra.finance_tracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ErrorResponse {
    // Getters and setters
    private String message;
    private List<String> details;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }
}
