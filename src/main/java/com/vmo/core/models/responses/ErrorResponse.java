package com.vmo.core.models.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmo.core.common.error.ApiException;
import com.vmo.core.common.error.ErrorCode;
import com.vmo.core.common.messages.MessageData;
import com.vmo.core.common.messages.MessageUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;

import java.util.Objects;

@Data
public class ErrorResponse {
    @JsonIgnore
    private final ErrorCode errorCode;
    private final String message;
    private final String messageCode;
//    @JsonIgnore
//    private Object[] messageArguments;
    private LocalDateTime time;
    private String path;
    private String httpMethod;

    public ErrorResponse(ApiException exception) {
        errorCode = exception.getErrorCode();
        if (StringUtils.isNotEmpty(exception.getMessageCode())) {
            message = MessageUtils.getMessage(exception.getMessageCode(), exception.getMessageArguments());
            messageCode = exception.getMessageCode();
        } else {
            MessageData messageData = MessageUtils.parse(exception.getMessage(), exception.getMessageArguments());
            message = messageData.getMessage();
            messageCode = messageData.getMessageCode();
        }
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        if (ErrorCode.NULL_ERROR.equals(errorCode) && message == null) {
            this.message = null;
            this.messageCode = null;
        } else {
            MessageData messageData = MessageUtils.parse(message);
            this.message = messageData.getMessage();
            this.messageCode = messageData.getMessageCode();
        }
    }

    public ErrorResponse(ErrorCode errorCode, String messageCode, Object... messageArguments) {
        this.errorCode = errorCode;
        this.message = MessageUtils.getMessage(messageCode, messageArguments);
        this.messageCode = messageCode;
//        if (!Objects.equals(this.message, messageCode)) {
//            MessageUtils.getMessage(messageCode);
//        }
//        this.messageArguments = messageArguments;
    }

    @JsonProperty("errorCode")
    public String getErrorCodeName() {
        return errorCode != null ? errorCode.getCode() : null;
    }

//    @JsonProperty("message")
//    public String getMessage() {
//        if (message != null || StringUtils.isBlank(messageCode)) {
//            return message;
//        }
//        return messageCode;
//    }

//    public void setMessageCode(String messageCode) {
//        message = MessageUtils.getMessage(messageCode);
//        if (!Objects.equals(messageCode, message)) {
//            this.messageCode = messageCode;
//        }
//    }
}
