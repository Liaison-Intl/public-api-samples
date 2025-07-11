/**
 * Request class for Application-related API operations.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.liaisonedu.entity.ApiAppSubmissionIdResponseDTO;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liaisonedu.client.ApiClient;
import com.liaisonedu.entity.ApplicationSubmission;

public class ApplicationRequest extends BaseRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRequest.class);

    private String APPS_BY_FORM_ENDPOINT;
    private String APPS_BY_ORG_ENDPOINT;
    private String APP_BY_FORM_ENDPOINT;
    private String APP_BY_ORG_ENDPOINT;
    private String APP_BY_ORG_AND_PROG_ENDPOINT;

    public ApplicationRequest(ApiClient<HttpRequestBase, Map<String, Object>, Object> apiClient) {
        super(apiClient);
        this.APPS_BY_FORM_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/applications?fromDate={fromDate}&toDate={toDate}";
        this.APPS_BY_ORG_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations/{organizationId}/applications?fromDate={fromDate}&toDate={toDate}";
        this.APP_BY_FORM_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/applications/{applicationId}";
        this.APP_BY_ORG_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations/{organizationId}/applications/{applicationId}";
        this.APP_BY_ORG_AND_PROG_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations/{organizationId}/programs/{programId}/applications/{applicationId}";
    }

    public List<ApplicationSubmission> getApplications(String apiScope, String organizationId, 
    		String fromDate, String toDate) throws IOException {
        String url;
        if (apiScope.equalsIgnoreCase(API_SCOPE_ORG_VAL) || apiScope.equalsIgnoreCase(API_SCOPE_PROG_VAL)) {
            url = APPS_BY_ORG_ENDPOINT;
        } else {
        	// temporarily use org level until form level is implemented
            // url = APPS_BY_FORM_ENDPOINT;
            url = APPS_BY_ORG_ENDPOINT;
        }

        ApiAppSubmissionIdResponseDTO appSubmissionResponseDTO = get("GET Application List", url,
                mapOf(APPLICATION_FORM_ID_PARAMETER, formId, ORGANIZATION_ID_PARAMETER, organizationId,
                        FROM_DATE_PARAMETER, emptyIfNull(fromDate), TO_DATE_PARAMETER, emptyIfNull(toDate)),
                ApiAppSubmissionIdResponseDTO.class, LOGGER);
       return appSubmissionResponseDTO.getApplications();

//        return getList("GET Application List", url,
//                mapOf(APPLICATION_FORM_ID_PARAMETER, formId, ORGANIZATION_ID_PARAMETER, organizationId,
//                        FROM_DATE_PARAMETER, emptyIfNull(fromDate), TO_DATE_PARAMETER, emptyIfNull(toDate)),
//                    ApplicationSubmission.class, LOGGER);
    }

    public Object getApplication(String apiScope, long applicationId, String organizationId, String programId) throws IOException {
        String url;
        if (apiScope.equalsIgnoreCase(API_SCOPE_PROG_VAL)) {
            url = APP_BY_ORG_AND_PROG_ENDPOINT;
        } else if (apiScope.equalsIgnoreCase(API_SCOPE_ORG_VAL)) {
            url = APP_BY_ORG_ENDPOINT;
        } else {
            url = APP_BY_FORM_ENDPOINT;
        }

        return get("GET Application", url, 
        		mapOf(APPLICATION_FORM_ID_PARAMETER, formId, ORGANIZATION_ID_PARAMETER, organizationId,
        				PROGRAM_ID_PARAMETER, programId, APPLICATION_ID_PARAMETER, applicationId), Object.class, LOGGER);
    }
}
