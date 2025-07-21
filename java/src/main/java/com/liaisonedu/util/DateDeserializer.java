/**
 * Custom Jackson deserializer for Date objects.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.liaisonedu.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateDeserializer extends JsonDeserializer<Date> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateDeserializer.class);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(Constants.DATE_FORMAT);

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        Date date = null;
        try {
            date = SIMPLE_DATE_FORMAT.parse(jsonParser.getText());
        } catch (ParseException e) {
            LOGGER.error("Failed to deserialize date." , e);
        }
        return date;
    }
}
