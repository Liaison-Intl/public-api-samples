/**
 * Entity representing Authorization information for API access.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Authorization {

    private String token;
    private String refreshToken;
    private String message;

    @JsonProperty("Token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @JsonProperty("RefreshToken")
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
