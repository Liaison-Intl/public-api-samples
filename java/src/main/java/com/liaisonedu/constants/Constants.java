/**
 * Application-wide constants.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.constants;

public interface Constants {

    String API_URL_KEY = "unicas.api.url";
    String API_X_KEY = "unicas.api.key";
    String API_SCOPE_KEY = "unicas.prop.apiScope";
    String APPLICATION_FORM_ID_KEY = "unicas.prop.applicationFormId";
    String ORGANIZATION_ID_KEY = "unicas.prop.organizationId";
    String PROGRAM_ID_KEY = "unicas.prop.programId";
    String PASSWORD_KEY = "unicas.api.password";
    String USERNAME_KEY = "unicas.api.username";
    String FROM_DATE_KEY = "unicas.prop.fromDate";
    String TO_DATE_KEY = "unicas.prop.toDate";
    String SKIP_FILES_KEY = "unicas.prop.skipFiles";
    String USE_GZIP_KEY = "unicas.prop.useGzip";

    String API_SCOPE_FORM_VAL = "form";
    String API_SCOPE_ORG_VAL = "org";
    String API_SCOPE_PROG_VAL = "prog";

    String RESPONSE_CODE = "Response Code";
    String RESPONSE_CONTENT_LENGTH = "Content-Length";
    String RESPONSE_ENCODING = "Encoding";
    String RESPONSE_ENTITY = "Entity";
    String RESPONSE_TIME = "ResponseTime";

    String APPLICATION_JSON = "application/json";
    String APPLICATION_PDF = "application/pdf";
    String HEADER_ACCEPT = "Accept";
    String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    String HEADER_AUTHORIZATION = "Authorization";
    String HEADER_CONTENT_TYPE = "Content-Type";
    String HEADER_X_API_KEY = "x-api-key";
    String GZIP = "gzip";

    String ENTITY_KEY = "Entity";

    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String DATE_FORMAT = "yyyy-MM-dd";

    String APPLICATION_FORM_ID_PARAMETER = "{applicationFormId}";
    String ORGANIZATION_ID_PARAMETER = "{organizationId}";
    String PROGRAM_ID_PARAMETER = "{programId}";
    String APPLICATION_ID_PARAMETER = "{applicationId}";
    String FROM_DATE_PARAMETER = "{fromDate}";
    String TO_DATE_PARAMETER = "{toDate}";
    String FILE_ID_PARAMETER = "{fileId}";
    String FILE_DOC_TYPE_PARAMETER = "{docType}";

    String TOKEN_EXPIRED = "The incoming token has expired";

    String ATTR_ID = "id";
    String ATTR_DOC_TYPE = "docType";
    String ATTR_RECEIVED_STATUS = "receivedStatus";

    String DOC_TYPE_TRANSCRIPT = "transcript";

    String J_PATH_APP_ID = "$..applicationId";

    String APPLICATION_LIMIT = "10";

    int SC_OK = 200;
    int TOO_MANY_REQUESTS = 429;
    int SC_RESPONSE_TOO_LARGE = 507;

    char OPT_PROPS = 'p';
    char OPT_PRINT_PROPS = 's';
    char OPT_HELP = 'h';
    char OPT_LIMIT = 'n';
    char OPT_OUTPUT = 'o';

    String FMT_ORG_LVL_APPS = "form_%s_org_%s_apps";
    String FMT_ORG_LVL_PROGRAMS = "form_%s_org_%s_programs";
    String FMT_ORG_LVL_PROGRAM_BRANDING = "form_%s_org_%s_program_branding";
    String FMT_APP = "form_%s_org_%s_app_%s";
    String FMT_FILE = "app_%s_%s_file_%s";
    String FMT_PROGRAM = "form_%s_org_%s_program_%s";


}
