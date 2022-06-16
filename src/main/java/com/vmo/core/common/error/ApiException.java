package com.vmo.core.common.error;

import com.vmo.core.common.messages.DefaultMessageCodes;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    @Getter
    private ErrorCode errorCode;
    @Getter
    private String messageCode;
    @Getter
    private Object[] messageArguments;

    public ApiException(Throwable cause, ErrorCode errorCode, String message) {
        super(message, cause);
        if (StringUtils.isBlank(message)) {
            throw new SystemException(cause, ErrorCode.UNCATEGORIZED_SERVER_ERROR, DefaultMessageCodes.ERROR_MESSAGE_CODE_NOT_EMPTY);
        }

        setErrorCode(errorCode);
    }

    public ApiException(Throwable cause, ErrorCode errorCode, String messageCode, Object... messageArguments) {
        super(messageCode, cause);
        if (StringUtils.isBlank(messageCode)) {
            throw new SystemException(cause, ErrorCode.UNCATEGORIZED_SERVER_ERROR, DefaultMessageCodes.ERROR_MESSAGE_CODE_NOT_EMPTY);
        }

        setErrorCode(errorCode);
        this.messageCode = messageCode;
        this.messageArguments = messageArguments;
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        if (StringUtils.isBlank(message)) {
            throw new SystemException(ErrorCode.UNCATEGORIZED_SERVER_ERROR, DefaultMessageCodes.ERROR_MESSAGE_CODE_NOT_EMPTY);
        }

        setErrorCode(errorCode);
    }

    public ApiException(ErrorCode errorCode, String messageCode, Object... messageArguments) {
        super(messageCode);
        if (StringUtils.isBlank(messageCode)) {
            throw new SystemException(ErrorCode.UNCATEGORIZED_SERVER_ERROR, DefaultMessageCodes.ERROR_MESSAGE_CODE_NOT_EMPTY);
        }

        setErrorCode(errorCode);
        this.messageCode = messageCode;
        this.messageArguments = messageArguments;
    }

    private void setErrorCode(ErrorCode errorCode) {
        if (errorCode == null
                && !getClass().equals(SystemException.class)
        ) {
            throw new SystemException(ErrorCode.UNCATEGORIZED_SERVER_ERROR, DefaultMessageCodes.ERROR_CODE_REQUIRED, null);
        }
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
