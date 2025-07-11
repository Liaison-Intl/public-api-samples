/**
 * Main application entry point for the Liaison Applicant API sample client.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.liaisonedu.client.UnicasApiClient;
import com.liaisonedu.constants.Constants;
import com.liaisonedu.entity.ApplicationSubmission;
import com.liaisonedu.entity.Organization;
import com.liaisonedu.entity.Program;
import com.liaisonedu.entity.ProgramBranding;
import com.liaisonedu.matcher.ConfigurationMatcher;
import com.liaisonedu.matcher.JsonPathMatcher;
import com.liaisonedu.matcher.LookupMatcher;
import com.liaisonedu.matcher.ProgramMatcher;
import com.liaisonedu.request.ApplicationRequest;
import com.liaisonedu.request.BaseRequest;
import com.liaisonedu.request.ConfigInfoRequest;
import com.liaisonedu.request.FileRequest;
import com.liaisonedu.request.OrganizationRequest;
import com.liaisonedu.request.ProgramRequest;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.cli.ParseException;
import org.beryx.textio.TextIoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private ApplicationConfiguration appConfig;
    private JsonSupport jsonSupport;
    private CmdLine cmd;

    public static void main(String[] args) {
        Main main = new Main();
        main.run(args);
    }

    private void run(String[] args) {
        try {
            cmd = new CmdLine(args);
        } catch (ParseException e) {
            LOGGER.error("Failed to parse command-line arguments", e);
            return;
        }

        if (cmd.getOption(Constants.OPT_HELP).isPresent()) {
            cmd.printHelp();
            System.exit(0);
        }

        appConfig = ApplicationConfiguration.get();

        if (cmd.getOption(Constants.OPT_PRINT_PROPS).isPresent()) {
            printProps();
            System.exit(0);
        }

        cmd.getOption(Constants.OPT_OUTPUT).ifPresent(path -> {
            try {
                checkAccessible(path);
            } catch (IOException e) {
                LOGGER.error("Output directory is not accessible", e);
                System.exit(-1);
            }
        });

        jsonSupport = JsonSupport.get();

        cmd.getOption(Constants.OPT_PROPS).ifPresent(props -> appConfig.initFromExternal(props));

        showWelcome();
        menu();
    }

    private void printProps() {
        System.out.println("List of supported properties:\n");
        appConfig.getPropKeys().forEach(System.out::println);
        System.out.println();
    }

    private void checkAccessible(String dir) throws IOException {
        Path path = Paths.get(dir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        } else if (!Files.isDirectory(path)) {
            throw new IOException(path + " is not a directory");
        } else if (!Files.isWritable(path)) {
            throw new IOException("Can't store files in " + path);
        }
    }

    private void showWelcome() {
        System.out.printf("%nWelcome to the Applicant API sample Java client! %n%n"
        		+ "Check your initial settings in the properties file: %n"
                + " Username: %s" + "%n"
                + " Application Form ID: %s" + "%n"
                + " API Scope: %s" + "%n",
                appConfig.getProperty(Constants.USERNAME_KEY),
                getFormId(),
                getApiScope());
        appConfig.checkInitialProperties();
    }

    private void menu() {
        while (true) {
            String description = "=====================================\n" +
                    "Please enter your choice (1, 2, 3, 4, 0)\n" +
                    "1. Exercise Applicant API suite\n" +
                    "2. Exercise full Applicant API suite (with Config and Lookups values)\n" +
                    "3. Exercise GET Programs/Program Branding suite\n" +
                    "4. Re-enter initial parameters\n" +
                    "0. Exit\n";

            String choice = TextIoFactory.getTextIO().newStringInputReader().read(description);

            switch (choice) {
                case "0":
                    System.exit(0);
                case "1":
                    System.out.println("Proceed to exercising Applicant API suite...");
                    timed(() -> callGetApplication(false));
                    break;
                case "2":
                    System.out.println("Proceed to exercising full Applicant API suite...");
                    timed(() -> callGetApplication(true));
                    break;
                case "3":
                    System.out.println("Proceed to exercising GET Programs/Program Branding suite...");
                    timed(this::callGetProgram);
                    break;
                case "4":
                    System.out.println("Re-enter the initial config...");
                    appConfig.enterNewProperties();
                    break;
                default:
                    System.out.println("Invalid input, please enter a choice (1, 2, 3, 4, 0).");
            }
        }
    }

    private void callGetApplication(boolean full) {
        try {
        	/* based on apiScope we can have multiple use cases:
        	 *  form: GET Application (form-level)
        	 *  org: GET Application (org-level)
        	 *  prog: GET Application (program-level)
        	*/
        	String apiScope = getApiScope();
            String formId = getFormId();
            String orgId = getOrgId(apiScope);
            String progId = getProgId(apiScope);

            UnicasApiClient unicasApiClient = buildApiClient();
            ApplicationRequest appRequest = new ApplicationRequest(unicasApiClient);

            List<ApplicationSubmission> applications = appRequest.getApplications(apiScope, orgId,
                    appConfig.getProperty(Constants.FROM_DATE_KEY), appConfig.getProperty(Constants.TO_DATE_KEY));

            if (applications.isEmpty()) {
                LOGGER.warn("No applications found for form id [{}], organization id [{}]", formId, orgId);
                LOGGER.info("Total request time [{}]ms",
                        requestTime(unicasApiClient.getTokenRequest(), appRequest));
                return;
            } else {
                LOGGER.info("Found [{}] applications for form id [{}], organization id [{}]", applications.size(), formId, orgId);
            }

            Map<String, Object> appMap = getApplications(apiScope, orgId, progId, applications, appRequest);

            ConfigInfoRequest configInfoRequest = new ConfigInfoRequest(unicasApiClient);
            ProgramRequest programRequest = new ProgramRequest(unicasApiClient);
            long configMatchDuration = 0;
            if (full && !appMap.isEmpty()) {
                Object configInfo = configInfoRequest.getConfigInfo(apiScope, orgId);
                Object lookups = configInfoRequest.getLookups(apiScope, orgId);
                List<Object> programsByOrganizations = programRequest.getProgramsByOrganization(apiScope, orgId);

                // skip this part unless we have some objects
                if (!programsByOrganizations.isEmpty() || null != lookups || null != configInfo) {
                	JsonPathMatcher configMatcher = setConfigurationMatcher(configInfo);
	                JsonPathMatcher lookupMatcher = setLookupMatcher(lookups);
	                JsonPathMatcher programMatcher = setProgramMatcher(programsByOrganizations);

	                Map<String, Object> fullAppMap = new LinkedHashMap<>();

                    appMap.forEach((name, app) -> {
                    	Object mappedApp = app;
	                	if (null != configMatcher) {
	                		mappedApp = configMatcher.match(mappedApp);
	                	}
	                	if (null != lookupMatcher) {
	                		mappedApp = lookupMatcher.match(mappedApp);
	                	}

	                	if (null != programMatcher) {
	                		mappedApp = programMatcher.match(mappedApp);
	                	}
                        fullAppMap.put(name + "_full", mappedApp);
                    });
	                appMap = fullAppMap;
	                configMatchDuration = matchingDuration(configMatcher, lookupMatcher, programMatcher);
                }
            }

            FileRequest fileRequest = new FileRequest(unicasApiClient);
            Map<String, byte[]> fileMap = getFiles(apiScope, orgId, appMap.values(), fileRequest);

            Optional<String> outputOpt = cmd.getOption(Constants.OPT_OUTPUT);
            if (outputOpt.isPresent()) {
                FileSupport fileSupport = new FileSupport(outputOpt.get(), jsonSupport);

                fileSupport.writeObjList(String.format(Constants.FMT_ORG_LVL_APPS, formId, orgId), applications);
                appMap.forEach(fileSupport::writeObj);
                fileMap.forEach(fileSupport::writePdf);
            } else {
            	String firstOrganizationText = "";
            	if(apiScope.equalsIgnoreCase(Constants.API_SCOPE_ORG_VAL) || apiScope.equalsIgnoreCase(Constants.API_SCOPE_PROG_VAL)) {
            		firstOrganizationText = "Organization Id: " + orgId + "%n";
            	}
                System.out.printf("========== Response Objects =========%n"
                                + firstOrganizationText
                                + "List of applications for the organization: " + "%n%s%n"
                                + "First application: " + "%n%s%n",
                        jsonSupport.write(applications),
                        jsonSupport.write(appMap.values().iterator().next()));
            }

            LOGGER.info("Total request time [{}]ms",
                    requestTime(unicasApiClient.getTokenRequest(), appRequest, fileRequest, configInfoRequest, programRequest));
            if (configMatchDuration > 0) {
                LOGGER.info("Total Configuration/Lookup mapping time [{}]ms", configMatchDuration);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to exercise GET Application suite. Check exceptions for more details.", e);
        }
    }

	protected JsonPathMatcher setProgramMatcher(List<Object> programsByOrganizations) {
		JsonPathMatcher programMatcher = null;
		if (!programsByOrganizations.isEmpty()) {
			programMatcher = new ProgramMatcher(programsByOrganizations);
		}
		return programMatcher;
	}

	protected JsonPathMatcher setLookupMatcher(Object lookups) {
		JsonPathMatcher lookupMatcher = null;
		if (null != lookups) {
			lookupMatcher = new LookupMatcher(lookups);
		}
		return lookupMatcher;
	}

	protected JsonPathMatcher setConfigurationMatcher(Object configInfo) {
		JsonPathMatcher configMatcher = null;
		if (null != configInfo) {
			configMatcher = new ConfigurationMatcher(configInfo);
		}
		return configMatcher;
	}

    private String getApiScope() {
    	List<String> scopes = Arrays.asList(Constants.API_SCOPE_FORM_VAL, 
    			Constants.API_SCOPE_ORG_VAL, Constants.API_SCOPE_PROG_VAL);
        String apiScope = appConfig.getProperty(Constants.API_SCOPE_KEY);
        if (!scopes.contains(apiScope)) {
            LOGGER.error("Invalid API Scope [{}]", apiScope);
            appConfig.updateProperty(Constants.API_SCOPE_KEY, null);
            return getApiScope();
        } else {
            return apiScope;
        }
    }

    private String getProgId(String apiScope) {
        String progId = appConfig.getProperty(Constants.PROGRAM_ID_KEY);
        if (apiScope.equalsIgnoreCase(Constants.API_SCOPE_PROG_VAL) && checkNumStr(progId)) {
            LOGGER.error("Invalid program id [{}]", progId);
            appConfig.updateProperty(Constants.PROGRAM_ID_KEY, null);
            return getProgId(apiScope);
        } else {
            return progId;
        }
    }

    private String getOrgId(String apiScope) {
        String orgId = appConfig.getProperty(Constants.ORGANIZATION_ID_KEY);
        if ((apiScope.equalsIgnoreCase(Constants.API_SCOPE_ORG_VAL) || apiScope.equalsIgnoreCase(Constants.API_SCOPE_PROG_VAL))
        		&& checkNumStr(orgId)) {
            LOGGER.error("Invalid organization id [{}]", orgId);
            appConfig.updateProperty(Constants.ORGANIZATION_ID_KEY, null);
            return getOrgId(apiScope);
        } else {
            return orgId;
        }
    }

	protected boolean checkNumStr(String numStr) {
		return numStr == null || numStr.isEmpty() || !Character.isDigit(numStr.charAt(0)) || Integer.parseInt(numStr) <= 0;
	}

    private String getFormId() {
    	return appConfig.getProperty(Constants.APPLICATION_FORM_ID_KEY);
    }

    private long requestTime(BaseRequest... requests) {
        return Arrays.stream(requests).mapToLong(BaseRequest::getTotalRequestDuration).sum();
    }

    private long matchingDuration(JsonPathMatcher... matchers) {
        return Arrays.stream(matchers).filter(Objects::nonNull).mapToLong(JsonPathMatcher::getTotalDuration).sum();
    }

    private Map<String, Object> getApplications(String apiScope, String orgId, String progId, 
    		List<ApplicationSubmission> applications, ApplicationRequest appRequest) throws IOException {
        String formId = getFormId();

        int limit = getLimit(applications.size());

        LOGGER.info("Fetching [{}] applications for form id [{}], organization id [{}]", limit, formId, orgId);
        Map<String, Object> appMap = new LinkedHashMap<>();
        for (int i = 0; i < limit; i++) {
            long appId = applications.get(i).getApplicationId();
            Object application = appRequest.getApplication(apiScope, appId, orgId, progId);
            if (application != null) {
                appMap.put(String.format(Constants.FMT_APP, formId, orgId, appId), application);
            }
        }
        return appMap;
    }

    private int getLimit(int countApps) {
        int limit = Integer.parseInt(cmd.getOption(Constants.OPT_LIMIT).orElse(Constants.APPLICATION_LIMIT));

        if (limit == -1) {
            return countApps;
        } else {
            return Math.min(countApps, limit);
        }
    }

    private Map<String, byte[]> getFiles(String apiScope, String orgId, Collection<Object> applications, FileRequest fileRequest) throws IOException {
        Map<String, byte[]> fileMap = new LinkedHashMap<>();

        if (Boolean.valueOf(appConfig.getProperty(Constants.SKIP_FILES_KEY))) {
            return fileMap;
        }

        for (Object app : applications) {
            List<Object> selectedId = JsonPath.read(app, Constants.J_PATH_APP_ID);
            Object appId = selectedId.get(0);
            List<Map<String,Object>> appFiles = JsonPath.read(app, "$..[?(@.docType)]");
            LOGGER.info("Found [{}] files for application id [{}]", appFiles.size(), appId);
            for (Map<String,Object> appFile : appFiles) {
                Integer fileId = (Integer) appFile.get(Constants.ATTR_ID);
                String docType = (String) appFile.get(Constants.ATTR_DOC_TYPE);

                if (Constants.DOC_TYPE_TRANSCRIPT.equals(docType)) {
                    Boolean receivedStatus = (Boolean) appFile.get(Constants.ATTR_RECEIVED_STATUS);
                    if (receivedStatus == null || !receivedStatus) {
                        LOGGER.warn("Skipping the [{}] file with id [{}] for application id [{}]. It is not received yet.",
                                docType, fileId, appId);
                        continue;
                    }
                }
                byte[] pdf = fileRequest.getFile(apiScope, orgId, fileId, docType);
                if (pdf != null) {
                    fileMap.put(String.format(Constants.FMT_FILE, appId, docType, fileId), pdf);
                }
            }
        }

        return fileMap;
    }

    private void timed(Runnable action) {
        long start = System.currentTimeMillis();
        action.run();
        LOGGER.info("Total execution time [{}]ms", System.currentTimeMillis() - start);
    }

    private UnicasApiClient buildApiClient() throws IOException {
         return new UnicasApiClient(
                 appConfig.getProperty(Constants.USERNAME_KEY),
                 appConfig.getProperty(Constants.PASSWORD_KEY));
    }

    private void callGetProgram(){
        try {
            // Initialize API Client
            UnicasApiClient unicasApiClient = buildApiClient();

            // Initialize Requests
            OrganizationRequest unicasOrganization = new OrganizationRequest(unicasApiClient);
            ProgramRequest unicasProgram = new ProgramRequest(unicasApiClient);

            String apiScope = getApiScope();
            String formId = getFormId();
            String organizationId = getOrgId(apiScope);
            String firstOrganizationText = "Organization Id: " + organizationId + "%n";

            /* based on apiScope we can have multiple use cases:
        	 *  form: GET First Program of First organization (form-level)
        	 *  org: GET First Program (org-level)
        	*/
            if (apiScope.equalsIgnoreCase(Constants.API_SCOPE_FORM_VAL)) {
	            List<Organization> listOfOrganizations = unicasOrganization.getOrganizations();

	            if (listOfOrganizations.isEmpty()) {
	                System.out.println("No data for the given application form id, please enter another.");
	                return;
	            }

	            Organization firstOrganization = listOfOrganizations.get(0);
	            firstOrganizationText = "First organization from application form ID: "
	                    + formId + "%n"
	            		+ jsonSupport.write(firstOrganization) + "%n";
	            organizationId = String.valueOf(firstOrganization.getId());
            }

            List<Program> listOfPrograms = unicasProgram.getPrograms(formId, organizationId);

            if (listOfPrograms.isEmpty()) {
                LOGGER.warn("No programs found for form id [{}], organization id [{}]", formId, organizationId);
                LOGGER.info("Total request time [{}]ms",
                        requestTime(unicasApiClient.getTokenRequest(), unicasOrganization, unicasProgram));
                return;
            }

            Program firstProgram = listOfPrograms.get(0);
            String programId = String.valueOf(firstProgram.getId());

            Program firstProgramDetailed = unicasProgram.getProgramById(organizationId, programId);
            List<ProgramBranding> brandingJsonList = unicasProgram.getProgramBranding(organizationId, programId);

            String orgId = organizationId;

            Optional<String> outputOpt = cmd.getOption(Constants.OPT_OUTPUT);
            if (outputOpt.isPresent()) {
                FileSupport files = new FileSupport(outputOpt.get(), jsonSupport);


                files.writeObjList(String.format(Constants.FMT_ORG_LVL_PROGRAMS, formId, orgId), listOfPrograms);
                files.writeObjList(String.format(Constants.FMT_ORG_LVL_PROGRAM_BRANDING, formId, orgId), brandingJsonList);
                files.writeObj(String.format(Constants.FMT_PROGRAM, formId, orgId, firstProgramDetailed.getId()), firstProgramDetailed);
            } else {
                System.out.printf("========== Response Objects =========%n"
                                + firstOrganizationText
                                + "List of programs for the organization: " + "%n%s%n"
                                + "First program detailed call: " + "%n%s%n"
                                + "First program branding: " + "%n%s%n",
                        jsonSupport.write(listOfPrograms),
                        jsonSupport.write(firstProgramDetailed),
                        jsonSupport.write(brandingJsonList));
            }

            LOGGER.info("Total request time [{}]ms",
                    requestTime(unicasApiClient.getTokenRequest(), unicasOrganization, unicasProgram));
        }catch (IOException e){
            LOGGER.error("Failed to retrieve data. Try again later, see exception: ", e);
        }
    }
}
