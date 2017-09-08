package ai.elimu.launcher_custom.model;

import java.util.List;

import ai.elimu.model.gson.admin.ApplicationGson;

public class AppGroup {

    private List<ApplicationGson> applications;

    public List<ApplicationGson> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationGson> applications) {
        this.applications = applications;
    }
}
