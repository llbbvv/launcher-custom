package ai.elimu.launcher_custom.util;

import ai.elimu.model.gson.project.AppCategoryGson;

public class PreferenceKeyHelper {

    public static String getPreferenceKey(AppCategoryGson appCategory) {
        String preferenceKey = "appCategoryId_" + appCategory.getId();
        return preferenceKey;
    }
}
