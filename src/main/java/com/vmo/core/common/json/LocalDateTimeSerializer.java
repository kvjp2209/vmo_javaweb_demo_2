package com.vmo.core.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
//	private static final SimpleDateFormat format = new SimpleDateFormat(FORMAT_DATE_TIME);

	@Override
	public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
			SerializerProvider serializerProvider) throws IOException {
//		jsonGenerator.writeString(format.format(localDateTime.toDate()));
		jsonGenerator.writeString(localDateTime.toString());
	}
}
