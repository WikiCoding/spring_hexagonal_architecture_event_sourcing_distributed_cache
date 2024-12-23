package com.wikicoding.inbound.rest.exceptions;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private final String message;
    private final int statusCode;
    private final String localDateTime;

    public ErrorResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.localDateTime = LocalDateTime.now().toString();
    }
}
