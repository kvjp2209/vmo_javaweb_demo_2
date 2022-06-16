package com.vmo.core.models.database.types.common;

import org.springframework.http.HttpMethod;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

//TODO only sql
@Converter(autoApply = true)
public class HttpMethodConverter implements AttributeConverter<HttpMethod, String> {
    @Override
    public String convertToDatabaseColumn(HttpMethod httpMethod) {
        if (httpMethod == null) return null;

        return httpMethod.name();
    }

    @Override
    public HttpMethod convertToEntityAttribute(String value) {
        return HttpMethod.resolve(value);
    }
}

