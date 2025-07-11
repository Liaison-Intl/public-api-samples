/**
 * JSON serialization and deserialization utilities.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JsonSupport {

    private static JsonSupport instance;
    private final ObjectMapper mapper;
    private final ObjectWriter writer;

    private JsonSupport() {
        this.mapper = new ObjectMapper();
        this.writer = mapper.writerWithDefaultPrettyPrinter();
    }

    public static JsonSupport get() {
        if (instance == null) {
            synchronized (JsonSupport.class) {
                if (instance == null) {
                    instance = new JsonSupport();
                }
            }
        }
        return instance;
    }

    public String write(Object obj) throws JsonProcessingException {
        return writer.writeValueAsString(obj);
    }

    public <T> T read(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    public <T> List<T> readList(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
    }
}
