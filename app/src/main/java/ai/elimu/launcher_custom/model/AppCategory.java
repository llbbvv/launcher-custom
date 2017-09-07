package ai.elimu.launcher_custom.model;

import java.util.List;

public class AppCategory {

    private String name;

    private List<AppGroup> appGroups;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AppGroup> getAppGroups() {
        return appGroups;
    }

    public void setAppGroups(List<AppGroup> appGroups) {
        this.appGroups = appGroups;
    }
}
