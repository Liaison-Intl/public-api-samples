package com.liaisonedu.entity;

import java.util.List;

public class ApiAppSubmissionIdResponseDTO {

    private List<ApplicationSubmission> applications;

    public List<ApplicationSubmission> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationSubmission> applications) {
        this.applications = applications;
    }
}

