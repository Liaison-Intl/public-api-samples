package com.liaisonedu.request;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liaisonedu.client.ApiClient;

public class ConfigInfoRequest extends BaseRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigInfoRequest.class);

    private String CONFIG_INFO_BY_FORM_ENDPOINT;
    private String CONFIG_INFO_BY_ORG_ENDPOINT;
    private String LOOKUPS_ENDPOINT;
    private String LOOKUPS_BY_ORG_ENDPOINT;

    public ConfigInfoRequest(ApiClient<HttpRequestBase, Map<String, Object>, Object> apiClient) {
        super(apiClient);
        this.CONFIG_INFO_BY_FORM_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/configInfo";
        this.CONFIG_INFO_BY_ORG_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations/{organizationId}/configInfo";
        this.LOOKUPS_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/lookups";
        this.LOOKUPS_BY_ORG_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations/{organizationId}/lookups";
    }

    public Object getConfigInfo(String apiScope, String organizationId) throws IOException {
        String url;
        if (apiScope.equalsIgnoreCase(API_SCOPE_ORG_VAL) || apiScope.equalsIgnoreCase(API_SCOPE_PROG_VAL)) {
        	url = CONFIG_INFO_BY_ORG_ENDPOINT;
        } else {
        	url = CONFIG_INFO_BY_FORM_ENDPOINT;
        }

        return getCompressed("GET Configuration", url,
                mapOf(APPLICATION_FORM_ID_PARAMETER, formId,
                    ORGANIZATION_ID_PARAMETER, organizationId), Object.class, LOGGER);
    }

    public Object getLookups(String apiScope, String organizationId) throws IOException {
        String url;
        if (apiScope.equalsIgnoreCase(API_SCOPE_ORG_VAL) || apiScope.equalsIgnoreCase(API_SCOPE_PROG_VAL)) {
        	url = LOOKUPS_BY_ORG_ENDPOINT;
        } else {
        	url = LOOKUPS_ENDPOINT;
        }

        return getCompressed("GET Lookups", url,
                mapOf(APPLICATION_FORM_ID_PARAMETER, formId,
                        ORGANIZATION_ID_PARAMETER, organizationId), Object.class, LOGGER);
    }
}
