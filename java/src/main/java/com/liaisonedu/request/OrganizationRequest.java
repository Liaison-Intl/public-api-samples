/**
 * Request class for Organization-related API operations.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.request;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.liaisonedu.client.ApiClient;
import com.liaisonedu.entity.Organization;

import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrganizationRequest extends BaseRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationRequest.class);

    private String ORGANIZATION_ENDPOINT;

    public OrganizationRequest(ApiClient<HttpRequestBase, Map<String, Object>, Object> apiClient) {
        super(apiClient);
        this.ORGANIZATION_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations";
    }

    public List<Organization> getOrganizations() throws IOException {
        return getList("GET Organizations", ORGANIZATION_ENDPOINT,
                Collections.singletonMap(APPLICATION_FORM_ID_PARAMETER, formId), Organization.class, LOGGER);
    }

}
