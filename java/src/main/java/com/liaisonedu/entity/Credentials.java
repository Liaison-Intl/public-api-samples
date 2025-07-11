/**
 * Entity representing user credentials for API authentication.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Credentials {

    private String username;
    private String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @JsonProperty("UserName")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("Password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
