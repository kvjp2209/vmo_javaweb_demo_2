package com.vmo.core.common.error;

public class SystemException extends ApiException {
    public SystemException(Throwable cause, ErrorCode errorCode, String message) {
        super(cause, errorCode, message);
    }

    public SystemException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public SystemException(ErrorCode errorCode, String messageCode, Object... messageArguments) {
        super(errorCode, messageCode, messageArguments);
    }
}
