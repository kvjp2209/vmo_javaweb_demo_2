package com.vmo.core.scheduler.exception;

public class WrappedJobException extends RuntimeException {
    public WrappedJobException(Throwable e) {
        super(e);
    }

    public WrappedJobException(Throwable e, String message) {
        super(message, e);
    }
}
