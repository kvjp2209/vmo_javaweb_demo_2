package com.vmo.core.common.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmo.core.common.messages.DefaultMessageCodes;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public final class ErrorCode {
    private static final Map<String, ErrorCode> allErrorCodes = new HashMap<>();

    //common error codes

    public static final ErrorCode UNAUTHORIZED = new ErrorCode("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    public static final ErrorCode NO_PERMISSION = new ErrorCode("NO_PERMISSION", HttpStatus.FORBIDDEN);
    public static final ErrorCode TOKEN_EXPIRED = new ErrorCode("TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED);

    public static final ErrorCode NOT_IMPLEMENTED_HTTP_METHOD = new ErrorCode("NOT_IMPLEMENTED_HTTP_METHOD", HttpStatus.METHOD_NOT_ALLOWED);
    public static final ErrorCode INVALID_PARAM = new ErrorCode("INVALID_PARAM", HttpStatus.UNPROCESSABLE_ENTITY);
    public static final ErrorCode INVALID_FORMAT_TYPE = new ErrorCode("INVALID_FORMAT_TYPE", HttpStatus.UNPROCESSABLE_ENTITY);

    public static final ErrorCode RESOURCE_NOT_FOUND = new ErrorCode("RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
    public static final ErrorCode RESOURCE_DELETED = new ErrorCode("RESOURCE_DELETED", HttpStatus.NOT_FOUND);
    public static final ErrorCode RESOURCE_DUPLICATE = new ErrorCode("RESOURCE_DUPLICATE", HttpStatus.CONFLICT);

    public static final ErrorCode INVALID_ACTION = new ErrorCode("INVALID_ACTION", HttpStatus.BAD_REQUEST);
    public static final ErrorCode UNSATISFIED_CONDITION = new ErrorCode("UNSATISFIED_CONDITION", HttpStatus.UNPROCESSABLE_ENTITY);

    public static final ErrorCode RATE_LIMIT_EXCEED = new ErrorCode("RATE_LIMIT_EXCEED", HttpStatus.TOO_MANY_REQUESTS);
    public static final ErrorCode SERVICE_TEMPORARY_UNAVAILABLE = new ErrorCode("SERVICE_TEMPORARY_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE);

    public static final ErrorCode EXTERNAL_SERVICE_FAIL = new ErrorCode("EXTERNAL_SERVICE_FAIL", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final ErrorCode NULL_ERROR = new ErrorCode("NULL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

    public static final ErrorCode UNCATEGORIZED_CLIENT_ERROR = new ErrorCode("UNCATEGORIZED_CLIENT_ERROR", HttpStatus.BAD_REQUEST);
    public static final ErrorCode UNCATEGORIZED_SERVER_ERROR = new ErrorCode("UNCATEGORIZED_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

    //end

    @Getter
    private String code;
    @Getter
    @JsonIgnore
    private HttpStatus httpStatus;

    public ErrorCode(String code, HttpStatus status) {
        if (allErrorCodes.containsKey(code)) {
            throw new SystemException(
                    UNCATEGORIZED_SERVER_ERROR,
                    DefaultMessageCodes.ERROR_CODE_DUPLICATED,
                    code
            );
        }

        this.code = code;
        this.httpStatus = status;

        allErrorCodes.put(code, this);
    }
}
