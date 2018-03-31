package ai.elimu.launcher_custom.util;

import ai.elimu.model.gson.project.AppCategoryGson;
import timber.log.Timber;

public class PreferenceKeyHelper {

    public static String getPreferenceKey(AppCategoryGson appCategory) {
        String preferenceKey = "appCategoryId_" + appCategory.getId();
        Timber.i("preferenceKey: " + preferenceKey);
        return preferenceKey;
    }
}
