package com.vmo.core.common.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmo.core.common.logging.api.InboundApiLogger;
import com.vmo.core.common.messages.DefaultMessageCodes;
import com.vmo.core.common.messages.MessageUtils;
import com.vmo.core.common.utils.action.ActionResult;
import com.vmo.core.integration.slack.SlackWebhookService;
import com.vmo.core.models.responses.ErrorResponse;
//import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class ErrorHandlingController {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandlingController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SlackWebhookService slackWebhookService;
    @Autowired
    private InboundApiLogger inboundApiLogger;

    //TODO refactor use message code

    //region Client errors
//    @ExceptionHandler(ClientAbortException.class)
//    public void handleDisconnected(HttpServletRequest request, HttpServletResponse response, ClientAbortException e) {
//        ErrorResponse errorResponse = new ErrorResponse(
//                ErrorCode.UNCATEGORIZED_CLIENT_ERROR,
//                e.getMessage() //this case doesnt need message code, because cant response to client
//        );
//        errorResponse.setTime(LocalDateTime.now());
//        errorResponse.setPath(request.getServletPath());
//        errorResponse.setHttpMethod(request.getMethod());

        //non-standard status https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#Unofficial_codes
        //nginx status 499
//        return ResponseEntity.status(499).body(errorResponse);

//        try {
//            LOG.warn(objectMapper.writeValueAsString(errorResponse));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleWrongType(HttpServletRequest request, HttpMediaTypeNotAcceptableException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.UNCATEGORIZED_CLIENT_ERROR,
                e.getMessage() //TODO
        );
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setHttpMethod(request.getMethod());

        increaseClientError(request, errorResponse);

        return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleWrongHttpMethod(
            HttpServletRequest request, HttpRequestMethodNotSupportedException e
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.NOT_IMPLEMENTED_HTTP_METHOD,
                e.getMessage() //TODO
        );
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setHttpMethod(request.getMethod());
        increaseClientError(request, errorResponse);

        return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolation(
            HttpServletRequest req, ConstraintViolationException e
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_PARAM,
                e.getMessage() //TODO
        );

        if (CollectionUtils.isNotEmpty(e.getConstraintViolations())) {
            ConstraintViolation violation = (ConstraintViolation) e.getConstraintViolations().toArray()[0];
            Path paramPath = violation.getPropertyPath();
            if (paramPath != null) {
                String paramPathString = paramPath.toString();
                String param = paramPathString.contains(".")
                        ? paramPathString.substring(paramPathString.lastIndexOf(".") + 1)
                        : paramPathString;
                errorResponse = new ErrorResponse(
                        ErrorCode.INVALID_PARAM,
                        param + " is required" //TODO
                );
            }
        }
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(req.getServletPath());
        errorResponse.setHttpMethod(req.getMethod());
        increaseClientError(req, errorResponse);

        return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> httpMessageNotReadableException(
            HttpServletRequest request, HttpMessageNotReadableException e
    ) {
        e.printStackTrace();

        String message;
        if (e.getCause() != null && e.getCause() instanceof JsonMappingException) {
            message = e.getCause().getMessage();
        } else {
            message = e.getMessage();
        }

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_PARAM,
                message + " (Input data invalid)" //TODO
        );
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setHttpMethod(request.getMethod());
        increaseClientError(request, errorResponse);

        return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleParameterFormatException(
            HttpServletRequest request, MethodArgumentTypeMismatchException e
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_PARAM,
                "Invalid format value"
                        + ". Parameter: " + e.getName() +
                        (e.getValue() == null ? "" : ", value: " + e.getValue()) //TODO
        );
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setHttpMethod(request.getMethod());
        increaseClientError(request, errorResponse);

        return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            HttpServletRequest request, MethodArgumentNotValidException e
    ) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            //TODO field error isnt just about required field, must handle other case too
            ErrorResponse errorResponse = new ErrorResponse(
                    ErrorCode.INVALID_PARAM,
                    fieldError.getField() + " is required"
            );
            errorResponse.setTime(LocalDateTime.now());
            errorResponse.setPath(request.getServletPath());
            errorResponse.setHttpMethod(request.getMethod());

            return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
        }
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_PARAM,
                "Error param"
        );
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setHttpMethod(request.getMethod());
        increaseClientError(request, errorResponse);

        return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            HttpServletRequest request, MissingServletRequestParameterException e
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.INVALID_PARAM,
                e.getMessage() //TODO
        );
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setHttpMethod(request.getMethod());
        increaseClientError(request, errorResponse);

        return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
    }

    //defined client error
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleExceptionResponse(HttpServletRequest request, ApiException e) {
        ErrorResponse errorResponse = new ErrorResponse(e);
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setHttpMethod(request.getMethod());

        try {
            LOG.warn(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        increaseClientError(request, errorResponse);

        return new ResponseEntity(errorResponse, e.getHttpStatus());
    }
    //endregion


    //region Server error

    //defined server error
    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ErrorResponse> handleServerException(HttpServletRequest request, SystemException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode() != null ? e.getErrorCode() : ErrorCode.UNCATEGORIZED_SERVER_ERROR,
                e.getMessage(), e.getMessageArguments()
        );
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setHttpMethod(request.getMethod());

        increaseServerError();
        saveLog(request, e, errorResponse);

        return new ResponseEntity(errorResponse, e.getHttpStatus());
    }

    //all other server error
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(HttpServletRequest request, NullPointerException e) {
        e.printStackTrace();

        String message = e.getMessage() == null ? getDefaultErrorMessage() : e.getMessage();
//        if (e.getMessage() == null) {
            ErrorResponse errorResponse = new ErrorResponse(
                    ErrorCode.NULL_ERROR, message
            );
            errorResponse.setTime(LocalDateTime.now());
            errorResponse.setPath(request.getServletPath());
            errorResponse.setHttpMethod(request.getMethod());

            increaseServerError();
            saveLog(request, e, errorResponse);

            return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
//        }

//        return exceptionHandler(request, (Exception) e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(HttpServletRequest request, Exception e) {
        e.printStackTrace();

        String defaultMessage = MessageUtils.getMessage(DefaultMessageCodes.UNKOWN_SERVER_ERROR);
        String message;
        if (Objects.equals(defaultMessage, DefaultMessageCodes.UNKOWN_SERVER_ERROR)) {
            message = e.getMessage();
        } else {
            message = defaultMessage;
        }

        ErrorResponse errorResponse = new ErrorResponse(
                ErrorCode.UNCATEGORIZED_SERVER_ERROR,
                message
        );
        errorResponse.setTime(LocalDateTime.now());
        errorResponse.setPath(request.getServletPath());
        errorResponse.setHttpMethod(request.getMethod());

        increaseServerError();
        saveLog(request, e, errorResponse);

        return new ResponseEntity(errorResponse, errorResponse.getErrorCode().getHttpStatus());
    }

    private String getDefaultErrorMessage() {
        String defaultMessage = MessageUtils.getMessage(DefaultMessageCodes.UNKOWN_SERVER_ERROR);
        if (Objects.equals(defaultMessage, DefaultMessageCodes.UNKOWN_SERVER_ERROR)) {
            return null;
        } else {
            return defaultMessage;
        }
    }
    //endregion



    //region Integration
    private void saveLog(HttpServletRequest request, Exception e, ErrorResponse error) {
        try {
            inboundApiLogger.logApiError(request, e, error);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    private void increaseClientError(HttpServletRequest request, ErrorResponse e) {
        checkCycle(() -> {
            slackWebhookService.increaseClientError(request, e);
            return true;
        });
    }

    private void increaseServerError() {
        checkCycle(() -> {
            slackWebhookService.setServerErrors(slackWebhookService.getServerErrors() + 1);
            return true;
        });
    }

    private void checkCycle(ActionResult callback) {
        if (slackWebhookService.isNewCycle()) {
            slackWebhookService.alertErrorAsync(
                    () -> {
                        slackWebhookService.resetCounter();
                        if (callback != null) {
                            callback.call();
                        }
                        return true;
                    }
            );
        } else {
            if (callback != null) {
                callback.call();
            }
            slackWebhookService.alertErrorAsync(null);
        }
    }
    //endregion
}
