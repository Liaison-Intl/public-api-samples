package com.liaisonedu.matcher;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.liaisonedu.constants.LookupConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class LookupMatcher extends JsonPathMatcher implements LookupConstants {

    private DocumentContext lookups;

    public LookupMatcher(Object lookupJson) {
        this.lookups = JsonPath.parse(lookupJson);
    }

    @Override
    Object matchApplication(Object application) {
        DocumentContext app = JsonPath.parse(application);

        List<String> lookupIds = Arrays
                .asList(COUNTRY, ORGANIZATION_COUNTRY, COUNTRY_ID, COUNTRY_OF_CITIZENSHIP_ID,
                        OTHER_COUNTRY_OF_CITIZENSHIP_ID, COUNTY, COUNTY_ID, STATE, ORGANIZATION_STATE,
                        STATE_ID, MAJOR_ID, SECOND_MAJOR_ID, MINOR_ID, DEGREE_STATUS_ID,
                        CURRENT_MILITARY_STATUS_ID, MILITARY_STATUS_ID, ARMED_FORCES_ID,
                        GEOGRAPHIC_AREA_RAISED, NATIVE_LANGUAGE_ID, ALT_TYPE_ID, STATUS_ID,
                        HIGHEST_EDU_LEVEL, OCCUPATION, RELATIONSHIP_TYE, TELEPHONE_TYPE_ID,
                        ALT_PHONE_TYPE_ID, TERM_TYPE_ID, LANGUAGE_ID, LIVED_STATE, INDICATOR_ID, TERM,
                        TERM_TYPE, ACADEMIC_STATUS, GRE_SUBJECT_TEST_TYPE_ID, ISSUE_COUNTRY,
                        TYPE_OF_VISA, APTA_GROUP_TYPE_ID, LICENSE_TYPE_ID);

        lookupIds.forEach(lookupId -> matchLookupsById(lookupId, app));

        Stream.of(RESPONSE_VALUE_ID).forEach(valueIdLookup -> matchLookupsByValue(valueIdLookup, app));

        matchSpecific(app);

        return app.json();
    }

    private void matchLookupsById(String lookupField, DocumentContext app) {
        matchLookupsInternal(lookupField, this::findLookupValueById, app);
    }

    private void matchLookupsByValue(String valueIdLookup, DocumentContext app) {
        matchLookupsInternal(valueIdLookup, this::findLookupValueByStringId, app);
    }

    private void matchLookupsInternal(String lookupField, Function<Object, Object> lookupValueFunc, DocumentContext app) {
        List<Map<String, Object>> matching = JsonPath.read((Object) app.json(), "$..[?(@." + lookupField + ")]");

        String newField;
        if (lookupField.contains("Id")) {
            newField = lookupField.replace("Id", "");
        } else {
            newField = lookupField + "Value";
        }

        for (Map<String, Object> obj : matching) {
            if (obj.containsKey(newField)) {
                continue;
            }

            Object lookupId = obj.get(lookupField);

            Object value = lookupValueFunc.apply(lookupId);

            if (value != null) {
                String destinationPath = "$..[?(@." + lookupField + "==" + getLookupIdStr(lookupId) + ")]";
                app.put(destinationPath, newField, value);
            }
        }
    }

    private Object findLookupValueById(Object lookupId) {
        if (isNumeric(lookupId)) {
            List<String> value = JsonPath.read((Object) lookups.json(), "$.lookupSets..[?(@.id==" + lookupId + ")].value");
            if (!value.isEmpty()) {
                return value.get(0);
            }
        }
        return null;
    }

    private Object findLookupValueByStringId(Object lookupId) {
        if (lookupId != null && !lookupId.toString().contains(" ")) {
            List<String> value = JsonPath.read((Object) lookups.json(),
                    "$.lookupSets..[?(@.code=='" + lookupId + "')].value");
            if (!value.isEmpty()) {
                return value.get(0);
            }
        }
        return null;
    }

    private void matchSpecific(DocumentContext app) {
        List<Object> typeIds = JsonPath.read((Object) app.json(), "$..degrees.[?(@." + TYPE_ID + ")]." + TYPE_ID);

        typeIds.forEach(typeId -> {
            Object value = findLookupValueById(typeId);
            if (value != null) {
                app.put("$..degrees.[?(@." + TYPE_ID + "==" + typeId + ")]", "degreeType", value);
            }
        });
    }
}
