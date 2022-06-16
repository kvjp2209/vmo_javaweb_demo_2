package com.vmo.core.common.logging.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmo.core.common.CommonConstants;
import com.vmo.core.common.config.CommonConfig;
import com.vmo.core.common.logging.BaseLogger;
import com.vmo.core.common.utils.CoreUtils;
import com.vmo.core.models.database.entities.log.LogInboundApi;
import com.vmo.core.models.responses.ErrorResponse;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;

@Component
public class InboundApiLogger extends BaseLogger<LogInboundApi> {
    @Autowired
    private CommonConfig commonConfig;
    @Autowired
    private ObjectMapper objectMapper;

    public InboundApiLogger(ApplicationContext applicationContext, @Autowired(required = false) HikariDataSource hikariDataSource) {
        super(applicationContext, hikariDataSource);
    }

    @Override
    public void reloadConfig() {
    }

    public void logApiError(HttpServletRequest request, Exception exception, ErrorResponse errorResponse) {
        if (!commonConfig.isLogApiError()) return;

        String query = request.getQueryString();
        String body = getRequestBody(request);
        HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
        String webPage = request.getHeader("referer");

        LogInboundApi logApiError = new LogInboundApi();
        logApiError.setService(commonConfig.getService());
        logApiError.setPath(request.getServletPath());
        logApiError.setHttpMethod(httpMethod);
        logApiError.setExceptionName(exception.getClass().getSimpleName());
        logApiError.setStacktrace(ExceptionUtils.getStackTrace(exception));
        logApiError.setRequestQuery(query);
        logApiError.setRequestBody(body);
        logApiError.setResponseBody(CoreUtils.warpJsonException(() -> objectMapper.writeValueAsString(errorResponse)));
        logApiError.setResponseHttpStatusCode(errorResponse.getErrorCode().getHttpStatus().value());
        logApiError.setPage(webPage);

        addLog(logApiError);

//        request.setAttribute();
    }

    private String getRequestBody(HttpServletRequest request) {
        String body = null;
        if (CommonConstants.cacheableBodyContentTypes.stream()
                .anyMatch(type -> StringUtils.containsIgnoreCase(request.getContentType(), type))
        ) {
            if (request instanceof ContentCachingRequestWrapper) {
                body = new String(((ContentCachingRequestWrapper) request).getContentAsByteArray());
            } else {
                LOG.error("Not a supported http request content. Can not log body request");
            }
        }
        return body;
    }
}
