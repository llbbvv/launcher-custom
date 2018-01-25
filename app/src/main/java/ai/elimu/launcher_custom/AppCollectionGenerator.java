package ai.elimu.launcher_custom;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ai.elimu.model.gson.admin.ApplicationGson;
import ai.elimu.model.gson.project.AppCategoryGson;
import ai.elimu.model.gson.project.AppCollectionGson;
import ai.elimu.model.gson.project.AppGroupGson;
import timber.log.Timber;

public class AppCollectionGenerator {

    public static AppCollectionGson loadAppCollectionFromJsonFile(File jsonFile) {
        Timber.i("loadAppCollectionFromJsonFile");

        AppCollectionGson appCollection = new AppCollectionGson();

        try {
            String jsonResponse = FileUtils.readFileToString(jsonFile, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonResponse);
            Timber.d("jsonObject: " + jsonObject);
            JSONObject jsonObjectAppCollection = jsonObject.getJSONObject("appCollection");
            Timber.d("jsonObjectAppCollection: " + jsonObjectAppCollection);

            Type type = new TypeToken<AppCollectionGson>(){}.getType();
            appCollection = new Gson().fromJson(jsonObjectAppCollection.toString(), type);
        } catch (JSONException e) {
            Timber.e(e);
        } catch (IOException e) {
            Timber.e(e);
        }

        return appCollection;
    }

    private static AppGroupGson loadAppGroup(String... packageNames) {
        AppGroupGson appGroup = new AppGroupGson();

        List<ApplicationGson> applications = new ArrayList<>();
        for (String packageName : packageNames) {
            ApplicationGson application = new ApplicationGson();
            application.setPackageName(packageName);
            applications.add(application);
        }
        appGroup.setApplications(applications);

        return appGroup;
    }
}
