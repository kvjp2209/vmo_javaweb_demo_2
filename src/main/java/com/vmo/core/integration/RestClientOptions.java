package com.vmo.core.integration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestClientOptions {
    private Boolean enableLog;
    private Boolean logRequestBody;
    private Boolean logResponseBody;

    private MediaType defaultMediaType;
    private String basicAuthUsername;
    private String basicAuthPassword;
    private Map<String, List<String>> defaultHeaders;
}
