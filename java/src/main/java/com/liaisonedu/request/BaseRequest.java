/**
 * Base class for API requests.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.request;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;

import com.liaisonedu.client.ApiClient;
import com.liaisonedu.util.ApplicationConfiguration;
import com.liaisonedu.constants.Constants;
import com.liaisonedu.util.JsonSupport;

public abstract class BaseRequest implements Constants {

    private static final String GZIP_SUFFIX = " (gzip)";
    private final String logTemplate;

    final String baseUrl;
    final JsonSupport jsonSupport;
    final int formId;
    final ApiClient<HttpRequestBase, Map<String, Object>, Object> apiClient;
    final boolean useGzip;
    private long totalRequestDuration;

    BaseRequest(ApiClient<HttpRequestBase, Map<String, Object>, Object> apiClient) {
        this.apiClient = apiClient;
        this.logTemplate = "{}. Status code [{}]. Request time [{}] ms. Content length [{}] bytes.";
        this.jsonSupport = JsonSupport.get();
        this.baseUrl = ApplicationConfiguration.get().getProperty(API_URL_KEY);
        this.useGzip = Boolean.valueOf(ApplicationConfiguration.get().getProperty(USE_GZIP_KEY));
        this.formId = Integer.parseInt(ApplicationConfiguration.get().getProperty(Constants.APPLICATION_FORM_ID_KEY));
    }

    boolean isSuccess(Object code) {
        return code instanceof Number && SC_OK == ((Number) code).intValue();
    }

    boolean isZero(Object code) {
        return code instanceof Number && 0 == ((Number) code).intValue();
    }

    boolean isTooManyReq(Object code) {
        return code instanceof Number && TOO_MANY_REQUESTS == ((Number) code).intValue();
    }

    private boolean isResponseTooLarge(Object code) {
        return code instanceof Number && SC_RESPONSE_TOO_LARGE == ((Number) code).intValue();
    }

    Map<String, Object> mapOf(Object... pairs) {
        if (pairs == null || pairs.length == 0 || pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Illegal number of arguments: " + Arrays.toString(pairs));
        }

        Map<String, Object> map = new LinkedHashMap<>(pairs.length / 2);
        for (int i = 0; i < pairs.length; i += 2) {
            map.put(pairs[i].toString(), pairs[i + 1]);
        }

        return Collections.unmodifiableMap(map);
    }

    String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    void logStats(String key, Map<String, Object> response, Logger logger) {
        Object respTimeMs = response.get(RESPONSE_TIME);
        if (respTimeMs instanceof Number) {
            totalRequestDuration += ((Number) respTimeMs).longValue();
        }
        logger.info(logTemplate, key, response.get(RESPONSE_CODE), respTimeMs, response.get(RESPONSE_CONTENT_LENGTH));
    }

    public long getTotalRequestDuration() {
        return totalRequestDuration;
    }

    <T> T get(String key, String url, Map<String, Object> params, Class<T> responseType, Logger logger) throws IOException {
        return get(key, url, params, useGzip, responseType, logger);
    }

    private <T> T get(String key, String url, Map<String, Object> params, boolean useGzip, Class<T> responseType, Logger logger) throws IOException {
        Map<String, Object> responseMap = getResponse(url, params, useGzip);

        logStats(key + keySuffix(useGzip), responseMap, logger);
        if (isTooManyReq(responseMap.get(RESPONSE_CODE))) {
        	// handle 429, "Too Many Requests"
        	logger.warn("Too Many Requests, retrying");
        	safeSleep(logger);
        	return get(key, url, params, useGzip, responseType, logger);
        } else if (isSuccess(responseMap.get(RESPONSE_CODE)) && !isZero(responseMap.get(RESPONSE_CONTENT_LENGTH))) {
            return jsonSupport.read(responseMap.get(RESPONSE_ENTITY).toString(), responseType);
        } else if (!useGzip && isResponseTooLarge(responseMap.get(RESPONSE_CODE))) {
            return getCompressed(key + GZIP_SUFFIX, url, params, responseType, logger);
        } else {
            logger.error(key + keySuffix(useGzip) + " request failed. Status code is [{}]", responseMap.get(RESPONSE_CODE));
            return null;
        }
    }

    <T> List<T> getList(String key, String url, Map<String, Object> params, Class<T> responseType, Logger logger) throws IOException {
        return getList(key, url, params, useGzip, responseType, logger);
    }

    private <T> List<T> getList(String key, String url, Map<String, Object> params, boolean useGzip, Class<T> responseType, Logger logger) throws IOException {
        Map<String, Object> responseMap = getResponse(url, params, useGzip);

        logStats(key + keySuffix(useGzip), responseMap, logger);
        if (isTooManyReq(responseMap.get(RESPONSE_CODE))) {
        	// handle 429, "Too Many Requests"
        	logger.warn("Too Many Requests, retrying");
        	safeSleep(logger);
        	return getList(key, url, params, useGzip, responseType, logger);
        } else if (isSuccess(responseMap.get(RESPONSE_CODE))) {
            return jsonSupport.readList(responseMap.get(RESPONSE_ENTITY).toString(), responseType);
        } else if (isResponseTooLarge(responseMap.get(RESPONSE_CODE))) {
            return getCompressedList(key + GZIP_SUFFIX, url, params, responseType, logger);
        } else {
            logger.error(key + keySuffix(useGzip) + " request failed. Status code is [{}]", responseMap.get(RESPONSE_CODE));
            return Collections.emptyList();
        }
    }

    private Map<String, Object> getResponse(String url, Map<String, Object> params, boolean useGzip) throws IOException {
        if (useGzip) {
            return apiClient.getRequestCompressed(url, params);
        } else {
            return apiClient.getRequest(url, params);
        }
    }

    <T> T getCompressed(String key, String url, Map<String, Object> params, Class<T> responseType, Logger logger) throws IOException {
        return get(key, url, params, true, responseType, logger);
    }

    private <T> List<T> getCompressedList(String key, String url, Map<String, Object> params, Class<T> responseType, Logger logger) throws IOException {
        return getList(key, url, params, true, responseType, logger);
    }

    String keySuffix(boolean useGzip) {
        return useGzip ? GZIP_SUFFIX : "";
    }
    
    void safeSleep(Logger logger) {
    	safeSleep(500, logger);
    }

    void safeSleep(long millis, Logger logger) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        	logger.warn("Exception during Thread#sleep invocation", e);
        }
    }

}
