/**
 * HTTP client for the Liaison UNICAS API.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import com.liaisonedu.entity.Authorization;
import com.liaisonedu.entity.Credentials;
import com.liaisonedu.request.TokenRequest;
import com.liaisonedu.util.ApplicationConfiguration;
import com.liaisonedu.constants.Constants;
import com.liaisonedu.util.JsonSupport;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnicasApiClient implements ApiClient<HttpRequestBase, Map<String, Object>, Object>, Constants {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnicasApiClient.class);

    private TokenRequest tokenRequest;
    private Authorization auth;
    private Credentials credentials;
    private HttpClient httpClient;
    private ApplicationConfiguration appConfig;
    private JsonSupport jsonSupport;

    public UnicasApiClient(String username, String password) throws IOException {
        this.httpClient = HttpClientBuilder.create().disableContentCompression().build();
        this.credentials = new Credentials(username, password);
        this.appConfig = ApplicationConfiguration.get();
        this.jsonSupport = JsonSupport.get();
        this.tokenRequest = new TokenRequest(this);
        this.auth = performAuthorization();
    }

    @Override
    public Map<String, Object> getRequest(String url, Map<String, Object> params) throws IOException {
        return executeRequest(new HttpGet(prepareUrl(url, params)));
    }

    @Override
    public Map<String, Object> getRequestCompressed(String url, Map<String, Object> params) throws IOException {
        HttpGet httpGet = new HttpGet(prepareUrl(url, params));
        httpGet.setHeader(HEADER_ACCEPT_ENCODING, GZIP);
        return executeRequest(httpGet);
    }

    @Override
    public Map<String, Object> postRequest(String url, Map<String, Object> params, Object parameters) throws IOException {
        HttpPost httpPost = new HttpPost(prepareUrl(url, params));
        StringEntity entity = new StringEntity(jsonSupport.write(parameters));
        httpPost.setEntity(entity);
        return executeRequest(httpPost);
    }

    @Override
    public Map<String, Object> postRequestCompressed(String url, Map<String, Object> params, Object parameters) throws IOException {
        HttpPost httpPost = new HttpPost(prepareUrl(url, params));
        StringEntity entity = new StringEntity(jsonSupport.write(parameters));
        httpPost.setEntity(entity);
        httpPost.setHeader(HEADER_ACCEPT_ENCODING, GZIP);
        return executeRequest(httpPost);
    }

    @Override
    public Map<String, Object> executeRequest(HttpRequestBase httpRequestBase) throws IOException {
        setHeaders(httpRequestBase);

        LOGGER.info("Sending request to {}", httpRequestBase.getURI());
        long start = System.currentTimeMillis();
        HttpResponse httpResponse = httpClient.execute(httpRequestBase);

        Map<String, Object> responseMap = new HashMap<>();
        Header contentEncoding = httpResponse.getEntity().getContentEncoding();
        if (contentEncoding != null) {
            responseMap.put(RESPONSE_ENCODING, contentEncoding.getValue());
        }

        responseMap.put(RESPONSE_ENTITY, readContent(httpResponse, contentEncoding));

        long contentLength = httpResponse.getEntity().getContentLength();
        if (contentLength > 0 && contentEncoding == null) {
            responseMap.put(RESPONSE_CONTENT_LENGTH, contentLength);
        } else {
            Object content = responseMap.get(RESPONSE_ENTITY);
            if (content instanceof String) {
                responseMap.put(RESPONSE_CONTENT_LENGTH, content.toString().length());
            } else {
                responseMap.put(RESPONSE_CONTENT_LENGTH, ((byte[]) content).length);
            }
        }
        responseMap.put(RESPONSE_CODE, httpResponse.getStatusLine().getStatusCode());
        responseMap.put(RESPONSE_TIME, System.currentTimeMillis() - start);

        if (isTokenExpired(responseMap.get(RESPONSE_ENTITY))) {
            executeRequest(httpRequestBase);
        }
        return responseMap;
    }

    private Object readContent(HttpResponse httpResponse, Header contentEncoding) throws IOException {
        Header contentType = httpResponse.getEntity().getContentType();
        if (contentType != null && APPLICATION_PDF.equals(contentType.getValue())) {
            return EntityUtils.toByteArray(httpResponse.getEntity());
        } else if (contentEncoding != null && contentEncoding.getValue().toLowerCase().contains(GZIP)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (GZIPInputStream gis = new GZIPInputStream(httpResponse.getEntity().getContent())) {
                byte[] buf = new byte[4096];
                int read;
                while ((read = gis.read(buf, 0, buf.length)) > 0) {
                    baos.write(buf, 0, read);
                }
            }

            return new String(baos.toByteArray());
        } else {
            return EntityUtils.toString(httpResponse.getEntity());
        }
    }

    private void setHeaders(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader(HEADER_ACCEPT, APPLICATION_JSON);
        httpRequestBase.setHeader(HEADER_CONTENT_TYPE, APPLICATION_JSON);
        httpRequestBase.setHeader(HEADER_X_API_KEY, appConfig.getProperty(API_X_KEY));
        if (auth != null) {
            httpRequestBase.setHeader(HEADER_AUTHORIZATION, auth.getToken());
        }
    }

    private String prepareUrl(String url, Map<String, Object> properties) {
        if (properties != null) {
            for (String key : properties.keySet()) {
                url = url.replace(key, String.valueOf(properties.get(key)));
            }
        }
        return url;
    }

    private boolean isTokenExpired(Object entity) throws IOException {
        // Pay attention that token, as well as the refresh token, has expiration time.
        // Here is the implementation of token refresh, whilst refresh token
        // requires its own implementation.
        if (entity instanceof String && ((String) entity).contains(TOKEN_EXPIRED)) {
            auth.setToken(performTokenRefresh().getToken());
            return true;
        }
        return false;
    }

    private Authorization performAuthorization() throws IOException {
        return tokenRequest.getToken(credentials);
    }

    private Authorization performTokenRefresh() throws IOException {
        return tokenRequest.refreshToken(auth);
    }

    public TokenRequest getTokenRequest() {
        return tokenRequest;
    }
}
