package com.vmo.core.integration.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.webhook.Payload;
import com.vmo.core.common.CommonConstants;
import com.vmo.core.integration.RestClient;
import com.vmo.core.integration.RestClientOptions;

public class MySlackApi extends RestClient {
    public MySlackApi(String serviceUrl, ObjectMapper objectMapper) {
        super(
                serviceUrl,
                RestClientOptions.builder()
                        .enableLog(false)
                        .build(),
                (int) CommonConstants.SECOND * 60
        );
        objectMapper(objectMapper);
    }

    public String send(Payload payload) {
        return post("", payload, String.class);
    }
}
