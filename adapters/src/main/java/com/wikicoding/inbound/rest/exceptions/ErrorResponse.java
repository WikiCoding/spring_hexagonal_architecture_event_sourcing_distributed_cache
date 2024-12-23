package com.wikicoding.inbound.rest.exceptions;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final String message;
    private final int statusCode;
    private final LocalDateTime localDateTime;

    public ErrorResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.localDateTime = LocalDateTime.now();
    }
}
