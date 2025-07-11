/**
 * Configuration data matcher for application data enrichment.
 *
 * @version 1.1
 * @since 2020
 */
package com.liaisonedu.matcher;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import com.liaisonedu.constants.ConfigConstants;

public class ConfigurationMatcher extends JsonPathMatcher implements ConfigConstants {

    private DocumentContext configInfo;

    public ConfigurationMatcher(Object configInfoJson) {
        this.configInfo = JsonPath.parse(configInfoJson);
    }

    @Override
    Object matchApplication(Object application) {
        DocumentContext app = JsonPath.parse(application);

        matchQuestions(app);
        matchCoursePrerequisites(app);
        matchGatewayDocuments(app);

        return app.json();
    }

    private void matchQuestions(DocumentContext app) {
        List<Map<String, Object>> matching = JsonPath.read((Object) app.json(), "$..[?(@." + QUESTION_ID + ")]");

        for (Map<String, Object> obj : matching) {
            Object questionId = obj.get(QUESTION_ID);
            List<String> questionText = JsonPath.read((Object) configInfo.json(), "$..questions.[?(@." + ID + "==" + questionId + ")].questionText");
            if (questionText.isEmpty()) {
                continue;
            }

            JsonPath target = JsonPath.compile("$..[?(@." + ID + "==" + obj.get(ID) + ")]");
            app.put(target, QUESTION, questionText.get(0));

            Object answerValue = obj.get(VALUE);
            if (answerValue == null) {
                answerValue = obj.get(STATEMENT_TEXT);
            }

            if (!isNumeric(answerValue)) {
                continue;
            }

            List<String> answerOption = JsonPath.read((Object) configInfo.json(), "$..questionDetails.[?(@." + ID + "==" + answerValue + ")].answerOption");
            if (answerOption.isEmpty()) {
                continue;
            }

            app.put(target, ANSWER, answerOption.get(0));
        }
    }

    private void matchCoursePrerequisites(DocumentContext app) {
        List<Map<String, Object>> matching = JsonPath.read((Object) app.json(), "$..[?(@." + PREREQUISITE_ID + ")]");

        for (Map<String, Object> obj : matching) {
            Object prereqId = obj.get(PREREQUISITE_ID);
            List<Map<String, Object>> prerequisites = JsonPath.read((Object) configInfo.json(), "$..coursePrerequisites.[?(@." + ID + "==" + prereqId + ")]");
            if (prerequisites.isEmpty()) {
                continue;
            }

            Map<String, Object> prereqConfig = prerequisites.get(0);
            JsonPath target = JsonPath.compile("$..[?(@." + PREREQUISITE_ID + "==" + prereqId + ")]");

            List<String> attrs = Arrays.asList(NAME, DESCRIPTION, MIN_GRADE, MIN_CREDITS);
            for (String attr : attrs) {
                if (prereqConfig.get(attr) != null) {
                    app.put(target, "prerequisite" + capitalize(attr), prereqConfig.get(attr));
                }
            }
        }
    }

    private void matchGatewayDocuments(DocumentContext app) {
        List<Map<String, Object>> matching = JsonPath.read((Object) app.json(), "$..gatewayAttachments.*");

        for (Map<String, Object> obj : matching) {
            Object docTypeId = obj.get(DOCUMENT_TYPE_ID);

            List<String> documentType = JsonPath.read((Object) configInfo.json(),
                    "$..gatewayDocuments.[?(@." + ID + "==" + docTypeId + ")]." + DOCUMENT_TYPE);
            if (documentType.isEmpty()) {
                continue;
            }

            app.put("$..gatewayAttachments.[?(@." + DOCUMENT_TYPE_ID + "==" + docTypeId + ")]",
                    DOCUMENT_TYPE, documentType.get(0));
        }
    }
}
