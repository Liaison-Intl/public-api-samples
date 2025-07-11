package com.liaisonedu.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.liaisonedu.constants.Constants;

public class TimestampSerializer extends JsonSerializer<Date> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimestampSerializer.class);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

	@Override
	public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		
		try {
			String formattedDate = SIMPLE_DATE_FORMAT.format(date);
			jsonGenerator.writeString(formattedDate);
		} catch(JsonGenerationException e) {
			LOGGER.info("Failed to serialize timestamp." , e);
		}
	}
}
