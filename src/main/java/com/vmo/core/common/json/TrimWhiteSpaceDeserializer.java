package com.vmo.core.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class TrimWhiteSpaceDeserializer extends StringDeserializer {
    private static final long serialVersionUID = -3562065572263950443L;
    private boolean emptyStringAsNull;

    public TrimWhiteSpaceDeserializer(boolean emptyStringAsNull) {
        this.emptyStringAsNull = emptyStringAsNull;
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();
        jsonParser.setCurrentValue(
                emptyStringAsNull
                ? ((StringUtils.isBlank(value)) ? null : value.trim())
                : (value == null ? null : value.trim())
        );

        return super.deserialize(jsonParser, deserializationContext);
    }
}
