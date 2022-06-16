package com.vmo.core.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vmo.core.common.CommonConstants;
import com.vmo.core.common.config.env.Environments;
import com.vmo.core.common.json.JsonObjectMapperCustomizer;
import com.vmo.core.common.json.TrimWhiteSpaceDeserializer;
import com.vmo.core.common.messages.MultipleResourceBundleMessageSource;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@ConfigurationProperties(prefix = CommonConstants.CONFIG_COMMON)
@ComponentScan({
        "com.vmo.core.common.error",
        "com.vmo.core.common.logging",
        "com.vmo.core.common.messages",
        "com.vmo.core.integration",
        "com.vmo.core.filters",
        "com.vmo.core.managers",
        "com.vmo.core.services",
        "com.vmo.core.controllers"
})
@Data
public class CommonConfig {
    private String service = "Unknown";
    private Environments env = Environments.LOCAL;
    private boolean requireAuthenticationByDefault = true;
    private boolean logApiError = false;
    private boolean logAsyncError = false;


    @Bean
    @Primary
    public ObjectMapper objectMapper(
            @Autowired(required = false) List<JsonObjectMapperCustomizer> customizers
    ) {
        return createObjectMapper(customizers);
    }

    @Bean
    public ObjectMapper controllerObjectMapper(
            WebConfig webConfig,
            @Autowired(required = false) List<JsonObjectMapperCustomizer> customizers
    ) {
        ObjectMapper objectMapper = createObjectMapper(customizers);

        //other converters
        SimpleModule commonModule = new SimpleModule();
        commonModule.addDeserializer(String.class, new TrimWhiteSpaceDeserializer(webConfig.getEmptyRequestStringAsNull()));

        objectMapper.registerModules(commonModule);

        return objectMapper;
    }

    @Bean
    public MessageSource messageSource() {
        MultipleResourceBundleMessageSource messageSource = new MultipleResourceBundleMessageSource();
        messageSource.setBasenames("classpath*:/locales/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setUseCodeAsDefaultMessage(true);

        Locale.setDefault(Locale.ENGLISH);

        return messageSource;
    }

    private ObjectMapper createObjectMapper(List<JsonObjectMapperCustomizer> customizers) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //time converters
//        SimpleModule javaTimeModule = new SimpleModule();
//        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
//        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

//        objectMapper.registerModules(javaTimeModule);

        List<Module> modules = new ArrayList<>();

        try {
            Class jdk8Class = ClassUtils.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module", ClassUtils.getDefaultClassLoader());
            Module jdk8 = (Module) BeanUtils.instantiateClass(jdk8Class);
            modules.add(jdk8);
        } catch (ClassNotFoundException e) {
        }

        try {
            Class javaTimeClass = ClassUtils.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", ClassUtils.getDefaultClassLoader());
            Module javaTime = (Module) BeanUtils.instantiateClass(javaTimeClass);
            modules.add(javaTime);
        } catch (ClassNotFoundException e) {
        }

        try {
            Class jodaClass = ClassUtils.forName("com.fasterxml.jackson.datatype.joda.JodaModule", ClassUtils.getDefaultClassLoader());
            Module joda = (Module) BeanUtils.instantiateClass(jodaClass);
            modules.add(joda);

            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        } catch (ClassNotFoundException e) {
        }

        if (CollectionUtils.isNotEmpty(customizers)) {
            customizers.forEach(c -> {
                c.customize(objectMapper, modules);
            });
        }

        if (CollectionUtils.isNotEmpty(modules)) {
            objectMapper.registerModules(modules);
        }

        return objectMapper;
    }
}
