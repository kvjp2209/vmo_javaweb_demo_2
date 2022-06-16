package com.vmo.core.scheduler.exception;

public class WrappedJobExecutionException extends WrappedJobException {
    public WrappedJobExecutionException(Throwable e) {
        super(e);
    }
}
