package com.vmo.core.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmo.core.common.CommonConstants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = CommonConstants.CONFIG_WEB)
//@EnableWebMvc
@ControllerAdvice
//@Data
public class WebConfig implements WebMvcConfigurer, WebBindingInitializer {
//    private final CommonConfig commonConfig;
    @Autowired
    @Qualifier("controllerObjectMapper")
    @Lazy
    private ObjectMapper objectMapper;

    @Getter @Setter
    private List<String> allowedOrigins;
    @Getter @Setter
    private Boolean emptyRequestStringAsNull = true;

//    @Autowired
//    public WebConfig(CommonConfig commonConfig, ObjectMapper objectMapper) {
//        this.commonConfig = commonConfig;
//        this.objectMapper = objectMapper;
//    }

    @InitBinder
    @Override
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor stringTrimmer = new StringTrimmerEditor(getEmptyRequestStringAsNull());
        binder.registerCustomEditor(String.class, stringTrimmer);;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.forEach(c -> {
            if (c instanceof MappingJackson2HttpMessageConverter) {
                ((MappingJackson2HttpMessageConverter) c).setObjectMapper(objectMapper);
            }
        });
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //TODO temp version: if common lang is old, StringUtils does not have isAllBlank()
        List<String> origins = getAllowedOrigins() == null
                ? null
                : getAllowedOrigins().stream()
                .filter(s -> StringUtils.isNotBlank(s))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(origins)
//                CollectionUtils.isNotEmpty(getAllowedOrigins())
//                && !StringUtils.isAllBlank(getAllowedOrigins().toArray(new String[0]))
        ) {
            registry.addMapping(BaseEndpoint.GENERAL_API_PATTERN)
                    .allowedOrigins(getAllowedOrigins().toArray(new String[0]))
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .exposedHeaders(
                            HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                            HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS,
                            HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
//                            HttpHeaders.AUTHORIZATION,
                            HttpHeaders.ORIGIN,
                            HttpHeaders.CONTENT_TYPE,
                            HttpHeaders.CONTENT_LENGTH,
                            HttpHeaders.CONTENT_DISPOSITION,
                            HttpHeaders.CONTENT_ENCODING
                    )
            ;
        }
    }

}
