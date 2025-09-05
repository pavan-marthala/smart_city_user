package com.smartcity.user.auth.exception;

import java.io.Serial;

public class AuthException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public AuthException(String message) {
        super(message);
    }
}
