package com.vmo.core.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmo.core.common.CommonConstants;
import com.vmo.core.common.logging.api.LoggingClientHttpRequestInterceptor;
import com.vmo.core.common.utils.CoreUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RestClient {
    private RestTemplate restTemplate;
    private String baseHost;
    private RestClientOptions options = new RestClientOptions();

    //region Setup
    public RestClient(String baseHost) {
        this(baseHost, null, (int) CommonConstants.MINUTE);
    }

    public RestClient(String baseHost, RestClientOptions customOptions, int timeout) {
        this.baseHost = baseHost;
        options.setDefaultMediaType(MediaType.APPLICATION_JSON);

        if (customOptions != null) {
            if (customOptions.getEnableLog() != null) {
                options.setEnableLog(customOptions.getEnableLog());
            }
            if (customOptions.getLogRequestBody() != null) {
                options.setLogRequestBody(customOptions.getLogRequestBody());
            }
            if (customOptions.getLogResponseBody() != null) {
                options.setLogResponseBody(customOptions.getLogResponseBody());
            }

            if (customOptions.getDefaultMediaType() != null) {
                options.setDefaultMediaType(customOptions.getDefaultMediaType());
            }
            if (customOptions.getBasicAuthUsername() != null) {
                basicAuthUsername(customOptions.getBasicAuthUsername());
            }
            if (customOptions.getBasicAuthPassword() != null) {
                basicAuthPassword(customOptions.getBasicAuthPassword());
            }
            if (customOptions.getDefaultHeaders() != null) {
                defaultHeaders(customOptions.getDefaultHeaders());
            }
        }

        init(timeout);
    }

    private void init(int timeout) {
        restTemplate = new RestTemplate();

//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        CoreHttpClientRequestFactory requestFactory = new CoreHttpClientRequestFactory();
        requestFactory.setConnectionRequestTimeout(timeout);
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

//        requestFactory.setHttpClient();

        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(requestFactory));

        if (restTemplate.getInterceptors() == null) {
            restTemplate.setInterceptors(new ArrayList<ClientHttpRequestInterceptor>());
        }
        restTemplate.getInterceptors().add(new LoggingClientHttpRequestInterceptor(
                options.getEnableLog(),
                options.getLogRequestBody(),
                options.getLogResponseBody()
        ));
    }

    public RestClient basicAuthUsername(String username) {
        options.setBasicAuthUsername(username);
        return this;
    }

    public RestClient basicAuthPassword(String password) {
        options.setBasicAuthPassword(password);
        return this;
    }

    public RestClient defaultHeaders(Map<String, List<String>> headers) {
        if (options.getDefaultHeaders() == null || headers == null) {
            options.setDefaultHeaders(headers);
        } else {
            options.getDefaultHeaders().putAll(headers);
        }
        return this;
    }

    public RestClient objectMapper(ObjectMapper objectMapper) {
        if (objectMapper != null) {
            MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
            jsonConverter.setPrettyPrint(false);
            jsonConverter.setObjectMapper(objectMapper);

            if (CollectionUtils.isNotEmpty(restTemplate.getMessageConverters())) {
                List<HttpMessageConverter> removeConverters = new ArrayList<>();
                for (HttpMessageConverter<?> messageConverter : restTemplate.getMessageConverters()) {
                    if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                        removeConverters.add(messageConverter);
                    }
                }

                if (CollectionUtils.isNotEmpty(removeConverters)) {
                    restTemplate.getMessageConverters().removeAll(removeConverters);
                }
            } else {
                restTemplate.setMessageConverters(new ArrayList<>());
            }
            restTemplate.getMessageConverters().add(0, jsonConverter);
        }
        return this;
    }

    //endregion


    //region Http method
    //GET
    public <T> T get(String endpoint, Class<T> response) {
        return get(endpoint, null, response);
    }

    public <T> T get(String endpoint, Map<String, Object> parameters, Class<T> response) {
        return get(endpoint,null, parameters, response, null);
    }

    public <T> T get(
            String endpoint, Map<String, List<String>> additionalHeaders,
            Map<String, Object> parameters,
            Class<T> response, MediaType mediaType
    ) {
        endpoint = convertUrlPathVariable(endpoint, parameters);
        HttpEntity entity = httpEntity(additionalHeaders, null, mediaType);
        String uri = convertQueryParams(endpoint, parameters);

        ResponseEntity<T> res = restTemplate.exchange(uri, HttpMethod.GET, entity, response);
        return res.getBody();
    }

    public <T> T get(String endpoint, ParameterizedTypeReference<T> response) {
        return get(endpoint, null, response);
    }

    public <T> T get(String endpoint, Map<String, Object> parameters, ParameterizedTypeReference<T> response) {
        return get(endpoint, null, parameters, response, null);
    }

    public <T> T get(
            String endpoint, Map<String, List<String>> additionalHeaders,
            Map<String, Object> parameters,
            ParameterizedTypeReference<T> response, MediaType mediaType
    ) {
        endpoint = convertUrlPathVariable(endpoint, parameters);
        HttpEntity entity = httpEntity(additionalHeaders, null, mediaType);
        String uri = convertQueryParams(endpoint, parameters);

        ResponseEntity<T> res = restTemplate.exchange(uri, HttpMethod.GET, entity, response);
        return res.getBody();
    }

    //some weird services use body for API GET
    public <T> T get(String endpoint, Object body, ParameterizedTypeReference<T> response) {
        return get(endpoint, null, null, body, response, null);
    }

    public <T> T get(
            String endpoint, Map<String, List<String>> additionalHeaders,
            Map<String, Object> parameters, Object body,
            ParameterizedTypeReference<T> response, MediaType mediaType
    ) {
        endpoint = convertUrlPathVariable(endpoint, parameters);
        HttpEntity entity = httpEntity(additionalHeaders, body, mediaType);
        String uri = convertQueryParams(endpoint, parameters);

        ResponseEntity<T> res = restTemplate.exchange(uri, HttpMethod.GET, entity, response);
        return res.getBody();
    }


    //POST
    public <T> T post(
            String endpoint, Object body, Class<T> response
    ) {
        return post(endpoint, null, body, response, null);
    }

    public <T> T post(
            String endpoint, Object body, Class<T> response, MediaType mediaType
    ) {
        return post(endpoint, null, body, response, mediaType);
    }

    public <T> T post(
            String endpoint, Map<String, List<String>> additionalHeaders, Object body,
            Class<T> response, MediaType mediaType
    ) {
        HttpHeaders headers = httpHeader(additionalHeaders, mediaType);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        ResponseEntity<T> res = restTemplate.exchange(baseHost + endpoint, HttpMethod.POST, entity, response);
        return res.getBody();
    }


    //PUT
    public <T> T put(
            String endpoint, Object body, Class<T> response
    ) {
        return put(endpoint, null, body, response, null);
    }

    public <T> T put(
            String endpoint, Map<String, List<String>> additionalHeaders, Object body,
            Class<T> response, MediaType mediaType
    ) {
        //default json
        HttpHeaders headers = httpHeader(additionalHeaders, mediaType);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        ResponseEntity<T> res = restTemplate.exchange(baseHost + endpoint, HttpMethod.PUT, entity, response);
        return res.getBody();
    }
    //endregion

    protected String encodeBasicAuth(String username, String password) {
        String credentials = username + ":" + password;
        return CommonConstants.HEADER_BASIC_AUTH_PREFIX + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    private HttpEntity httpEntity(Map<String, List<String>> additionalHeaders, Object body, MediaType mediaType) {
        return new HttpEntity(body, httpHeader(additionalHeaders, mediaType));
    }

    private HttpHeaders httpHeader(Map<String, List<String>> additionalHeaders, MediaType mediaType) {
        mediaType = mediaType != null ? mediaType : options.getDefaultMediaType();

        HttpHeaders headers = new HttpHeaders();

        if (MapUtils.isNotEmpty(options.getDefaultHeaders())) {
            headers.putAll(options.getDefaultHeaders());
        }

        if (MapUtils.isNotEmpty(additionalHeaders)) {
            additionalHeaders.forEach((k, v) -> {
                if (headers.containsKey(k)) {
                    headers.get(k).addAll(v);
                } else {
                    headers.put(k, v);
                }
            });
        }

        headers.setAccept(Collections.singletonList(mediaType));
        headers.setContentType(mediaType);

//        if (StringUtils.isNoneBlank(basicAuthUsername, basicAuthPassword)) {
        //TODO temp version support old apache common lang: no method isNoneBlank()
        if (StringUtils.isNotBlank(options.getBasicAuthUsername())
                && StringUtils.isNotBlank(options.getBasicAuthPassword())
        ) {
            headers.setBasicAuth(options.getBasicAuthUsername(), options.getBasicAuthPassword());
            //old spring dont have .setBasicAuth()
//            headers.set(CommonConstants.HEADER_BASIC_AUTH_NAME, encodeBasicAuth(basicAuthUsername, basicAuthPassword));
        }

        return headers;
    }

    protected static String convertUrlPathVariable(String url, Map<String, Object> parameters) {
        if (parameters == null) return url;

        Set<String> keys = parameters.keySet();
        List<String> keyArr = new ArrayList<>(keys);
        for (String key : keyArr) {
            String temKey = "{" + key + "}";
            if (url.contains(temKey)) {
                url = url.replace(temKey, parameters.get(key).toString());
                parameters.remove(key);
            }
        }
        return url;
    }

    protected String convertQueryParams(String endpoint, Map<String, Object> parameters) {
        String uri = baseHost + endpoint;
        if (MapUtils.isNotEmpty(parameters)) {
            uri = CoreUtils.addQueryParams(uri, parameters);
        }

        return uri;
    }
}
