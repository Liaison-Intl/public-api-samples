package com.liaisonedu.matcher;

public abstract class JsonPathMatcher {

    private long totalDuration;

    public long getTotalDuration() {
        return totalDuration;
    }

    abstract Object matchApplication(Object application);

    public Object match(Object application) {
        long start = System.currentTimeMillis();
        Object result = matchApplication(application);
        totalDuration += (System.currentTimeMillis() - start);

        return result;
    }

    boolean isNumeric(Object lookupId) {
        return lookupId instanceof Number
                || (lookupId instanceof String && validNumber(lookupId));
    }

    private boolean validNumber(Object lookupId) {
        try {
            Integer.parseInt(lookupId.toString());
            return true;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }

    String getLookupIdStr(Object lookupId) {
        if (isNumeric(lookupId)) {
            return lookupId.toString();
        } else {
            return "'" + lookupId + "'";
        }
    }

    String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
