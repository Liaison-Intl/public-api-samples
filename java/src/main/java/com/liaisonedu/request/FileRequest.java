/**
 * Request class for File-related API operations.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.request;

import java.io.IOException;
import java.util.Map;

import com.liaisonedu.util.S3Downloader;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liaisonedu.client.ApiClient;

public class FileRequest extends BaseRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileRequest.class);

    private String FILE_BY_FORM_ENDPOINT;
    private String FILE_BY_ORG_ENDPOINT;

    public FileRequest(ApiClient<HttpRequestBase, Map<String, Object>, Object> apiClient) {
        super(apiClient);
        this.FILE_BY_FORM_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/files/{fileId}?docType={docType}";
        this.FILE_BY_ORG_ENDPOINT = baseUrl + "/v1/applicationForms/{applicationFormId}/organizations/{organizationId}/files/{fileId}?docType={docType}";
    }

    public byte[] getFile(String apiScope, String organizationId, Integer fileId, String docType) throws IOException {
        String url;
        if (apiScope.equalsIgnoreCase(API_SCOPE_ORG_VAL) || apiScope.equalsIgnoreCase(API_SCOPE_PROG_VAL)) {
        	url = FILE_BY_ORG_ENDPOINT;
        } else {
        	url = FILE_BY_FORM_ENDPOINT;
        }

    	Map<String, Object> responseMap = apiClient.getRequest(url,
                mapOf(APPLICATION_FORM_ID_PARAMETER, formId,
                		ORGANIZATION_ID_PARAMETER, organizationId,
                        FILE_ID_PARAMETER, fileId,
                        FILE_DOC_TYPE_PARAMETER, docType));

        String key = "GET File - " + docType;
        logStats(key, responseMap, LOGGER);
        if (isTooManyReq(responseMap.get(RESPONSE_CODE))) {
        	// handle 429, "Too Many Requests"
        	LOGGER.warn("Too Many Requests, retrying");
        	safeSleep(LOGGER);
        	return getFile(apiScope, organizationId, fileId, docType);
        } else if (isSuccess(responseMap.get(RESPONSE_CODE))) {
            Object responseEntity = responseMap.get(RESPONSE_ENTITY);
            if (responseEntity instanceof String) {
                LOGGER.info("Downloading from s3");
                return S3Downloader.downloadFileAsBytes((String) responseEntity);
            } else {
                return (byte[]) responseEntity;
            }
//            return (byte[]) responseEntity;
        } else {
            LOGGER.error(key + " request failed. Status code is [{}]", responseMap.get(RESPONSE_CODE));
            return null;
        }
    }
}
