/**
 * Request class for authentication token operations.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.request;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liaisonedu.client.ApiClient;
import com.liaisonedu.entity.Authorization;
import com.liaisonedu.entity.Credentials;

public class TokenRequest extends BaseRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenRequest.class);

    private String TOKEN_ENDPOINT;
    private String REFRESH_TOKEN_ENDPOINT;

    public TokenRequest(ApiClient<HttpRequestBase, Map<String, Object>, Object> apiClient) {
        super(apiClient);
        this.TOKEN_ENDPOINT = baseUrl + "/v1/auth/token";
        this.REFRESH_TOKEN_ENDPOINT = baseUrl + "/v1/auth/token/refresh";
    }

    public Authorization getToken(Credentials credentials) throws IOException {
        LOGGER.info("Performing Authorization for [{}]", credentials.getUsername());
        Map<String, Object> response = postRequest(TOKEN_ENDPOINT, credentials);

        logStats("POST Token" + keySuffix(useGzip), response, LOGGER);
        if (isTooManyReq(response.get(RESPONSE_CODE))) {
        	// handle 429, "Too Many Requests"
        	LOGGER.warn("Too Many Requests, retrying");
        	safeSleep(LOGGER);
        	return getToken(credentials);
        } else if (isSuccess(response.get(RESPONSE_CODE))) {
            return jsonSupport.read(response.get(ENTITY_KEY).toString(), Authorization.class);
        } else {
            throw new IOException("Authorization is failed, please check your settings. Content: " + response.get(ENTITY_KEY).toString());
        }
    }

    public Authorization refreshToken(Authorization auth) throws IOException {
        LOGGER.info("Refreshing Token");
        Map<String, Object> response = postRequest(REFRESH_TOKEN_ENDPOINT, auth.getRefreshToken());

        logStats("POST Refresh Token" + keySuffix(useGzip), response, LOGGER);
        if (isTooManyReq(response.get(RESPONSE_CODE))) {
        	// handle 429, "Too Many Requests"
        	LOGGER.warn("Too Many Requests, retrying");
        	safeSleep(LOGGER);
        	return refreshToken(auth);
        } else if (isSuccess(response.get(RESPONSE_CODE))) {
            return jsonSupport.read(response.get(ENTITY_KEY).toString(), Authorization.class);
        } else {
            throw new IOException("Token refresh failed, please check your settings. Content: " + response.get(ENTITY_KEY).toString());
        }
    }

    private Map<String, Object> postRequest(String endpoint, Object body) throws IOException {
        if (useGzip) {
            return apiClient.postRequestCompressed(endpoint, null, body);
        } else {
            return apiClient.postRequest(endpoint, null, body);
        }
    }
}
