/**
 * Request class for Program-related API operations.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.liaisonedu.client.ApiClient;
import com.liaisonedu.entity.Program;
import com.liaisonedu.entity.ProgramBranding;

import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramRequest extends BaseRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramRequest.class);

    private String PROGRAM_BY_ORGANIZATION_ENDPOINT;
    private String PROGRAM_BY_FORM_ENDPOINT;
    private String PROGRAM_BY_ID_ENDPOINT;
    private String PROGRAM_BRANDING_ENDPOINT;

    public ProgramRequest(ApiClient<HttpRequestBase, Map<String, Object>, Object> apiClient) {
        super(apiClient);
        this.PROGRAM_BY_ORGANIZATION_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations/{organizationId}/programs";
        this.PROGRAM_BY_FORM_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/programs";
        this.PROGRAM_BY_ID_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations/{organizationId}/programs/{programId}";
        this.PROGRAM_BRANDING_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations/{organizationId}/programs/{programId}/branding";
    }

    public List<Program> getPrograms(String applicationFormId, String organizationId) throws IOException {
        return getList("GET Programs", PROGRAM_BY_ORGANIZATION_ENDPOINT,
                mapOf(APPLICATION_FORM_ID_PARAMETER, applicationFormId,
                        ORGANIZATION_ID_PARAMETER, organizationId), Program.class, LOGGER);
    }

    public List<Object> getProgramsByOrganization(String apiScope, String organizationId) throws IOException {
        String url;
        if (apiScope.equalsIgnoreCase(API_SCOPE_ORG_VAL) || apiScope.equalsIgnoreCase(API_SCOPE_PROG_VAL)) {
        	url = PROGRAM_BY_ORGANIZATION_ENDPOINT;
        } else {
        	url = PROGRAM_BY_FORM_ENDPOINT;
        }

        return getList("GET Programs By Organization", url,
                mapOf(APPLICATION_FORM_ID_PARAMETER, formId,
                		ORGANIZATION_ID_PARAMETER, organizationId), Object.class, LOGGER);
    }

    public Program getProgramById(String organizationId, String programId) throws IOException {
        return get("GET Program", PROGRAM_BY_ID_ENDPOINT,
                mapOf(APPLICATION_FORM_ID_PARAMETER, formId,
                        ORGANIZATION_ID_PARAMETER, organizationId,
                        PROGRAM_ID_PARAMETER, programId), Program.class, LOGGER);
    }

    public List<ProgramBranding> getProgramBranding(String organizationId, String programId) throws IOException {
        return getList("GET Program Branding", PROGRAM_BRANDING_ENDPOINT,
                mapOf(APPLICATION_FORM_ID_PARAMETER, formId,
                        ORGANIZATION_ID_PARAMETER, organizationId,
                        PROGRAM_ID_PARAMETER, programId), ProgramBranding.class, LOGGER);
    }
}
