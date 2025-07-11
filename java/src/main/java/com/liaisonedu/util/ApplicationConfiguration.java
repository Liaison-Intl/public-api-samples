/**
 * Application configuration management.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.util;

import com.liaisonedu.constants.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.beryx.textio.TextIoFactory;

public class ApplicationConfiguration implements Constants {

    private static ApplicationConfiguration instance;

    private ResourceBundle resourceBundle;

    private Map<String, String> properties;
    private List<String> required = Arrays.asList(API_URL_KEY, API_X_KEY,
            APPLICATION_FORM_ID_KEY, USERNAME_KEY, PASSWORD_KEY, API_SCOPE_KEY);

    private List<String> numeric = Arrays.asList(APPLICATION_FORM_ID_KEY, ORGANIZATION_ID_KEY, PROGRAM_ID_KEY);

    private ApplicationConfiguration() {
        resourceBundle = ResourceBundle.getBundle("application");
        properties = readProperties();
    }

    public static ApplicationConfiguration get() {
        if (instance == null) {
            synchronized (ApplicationConfiguration.class) {
                if (instance == null) {
                    instance = new ApplicationConfiguration();
                }
            }
        }
        return instance;
    }

    private Map<String, String> readProperties() {
        Map<String, String> props = new LinkedHashMap<>();
        List<String> propKeys = getPropKeys();

        propKeys.forEach(key -> props.put(key, readProperty(key)));

        return props;
    }

    List<String> getPropKeys() {
        return Arrays.asList(API_URL_KEY, API_X_KEY, APPLICATION_FORM_ID_KEY,
                ORGANIZATION_ID_KEY, PROGRAM_ID_KEY, API_SCOPE_KEY, 
                USERNAME_KEY, PASSWORD_KEY, FROM_DATE_KEY, TO_DATE_KEY, SKIP_FILES_KEY, USE_GZIP_KEY);
    }

    private String readProperty(String key) {
        if (resourceBundle.containsKey(key)) {
            return resourceBundle.getString(key);
        } else {
            return null;
        }
    }

    public String getProperty(String key){
        return properties.get(key);
    }

    void checkInitialProperties() {
        properties.entrySet().stream().filter(entry -> isRequired(entry.getKey())).forEach(
                entry -> updateProperty(entry.getKey(), entry.getValue()));
    }

    void updateProperty(String key, String value) {
        if (value == null || value.isEmpty()) {
            doUpdateProperty(key, value);
        }
    }

    private boolean isRequired(String key) {
        return required.contains(key);
    }

    private boolean isNumeric(String key) {
        return numeric.contains(key);
    }

    void enterNewProperties() {
        properties.forEach(this::doUpdateProperty);
    }

    private void doUpdateProperty(String key, String value) {
        if (isNumeric(key)) {
            int number = TextIoFactory.getTextIO().newIntInputReader()
                    .withDefaultValue(getDefaultNumber(value))
                    .read(key);
            properties.put(key, String.valueOf(number));
        } else {
            String newValue = TextIoFactory.getTextIO().newStringInputReader()
                    .withDefaultValue(getDefaultString(value))
                    .read(key);
            properties.put(key, newValue);
        }
    }

    private Integer getDefaultNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        } else {
            return Integer.parseInt(value.trim());
        }
    }

    private String getDefaultString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        } else {
            return value.trim();
        }
    }

    void initFromExternal(String location) {
        Path props = Paths.get(location);
        if (!Files.exists(props)) {
            return;
        }

        try (InputStream is = Files.newInputStream(props)) {
            resourceBundle = new PropertyResourceBundle(is);
            Map<String, String> mergedMap = new LinkedHashMap<>(properties);
            mergedMap.putAll(readProperties());
            properties = mergedMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
