package com.liaisonedu.matcher;

import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import com.liaisonedu.constants.ConfigConstants;

public class ProgramMatcher extends JsonPathMatcher implements ConfigConstants {

    private static final String ORG_NAME = "orgName";
    private static final String ORG_ID = "orgId";
    private static final String PROGRAM_ID = "programId";

    private DocumentContext programInfo;

    public ProgramMatcher(List<Object> programInfoJson) {
        this.programInfo = JsonPath.parse(programInfoJson);
    }

    @SuppressWarnings("unchecked")
    @Override
    Object matchApplication(Object application) {
        DocumentContext app = JsonPath.parse(application);

        List<Map<String, Object>> organizations = JsonPath.read((Object) app.json(), "$..organizations.*");

        organizations.forEach(org -> {

            Object orgId = org.get(ID);

            List<Map<String, Object>> programInfos = setProgramInfos(app, orgId);

            List<Map<String, Object>> programs = (List<Map<String, Object>>) org.get("programsSelected");

            programs.forEach(program -> {
                Object programId = program.get(PROGRAM_ID);
                programInfos.stream()
                        .filter(info -> info.get(ID).equals(programId))
                        .findAny()
                        .ifPresent(info -> app.put("$..programsSelected[?(@." + PROGRAM_ID + "==" + programId + ")]", NAME, info.get(NAME)));
            });

        });

        return app.json();
    }

	protected List<Map<String, Object>> setProgramInfos(DocumentContext app, Object orgId) {
		List<Map<String, Object>> programInfos = JsonPath.read((Object) programInfo.json(), "$..[?(@." + ORG_ID + "==" + orgId + ")]");
		if (!programInfos.isEmpty()) {
		    Object orgName = programInfos.get(0).get(ORG_NAME);
		    app.put("$..organizations[?(@." + ID + "==" + orgId + ")]", NAME, orgName);
		} else {
			// we will assume that this is the org level programInfo which doesn't have orgName or orgId
		    programInfos = JsonPath.read((Object) programInfo.json(), "$.*");
		}
		return programInfos;
	}
}
