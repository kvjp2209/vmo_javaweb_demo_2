package com.vmo.core.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
//    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern(FORMAT_DATE_TIME);

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        String content = jsonParser.getValueAsString();
        if (StringUtils.isBlank(content)) {
            return null;
        }

//        return LocalDateTime.parse(content, FORMATTER);
        return LocalDateTime.parse(content);
    }
}
