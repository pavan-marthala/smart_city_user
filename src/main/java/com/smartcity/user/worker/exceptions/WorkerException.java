package com.smartcity.user.worker.exceptions;

import java.io.Serial;

public class WorkerException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public WorkerException(String message) {
        super(message);
    }
}
