package ai.elimu.launcher_custom;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ai.elimu.launcher_custom.model.AppCategory;
import ai.elimu.launcher_custom.model.AppCollection;
import ai.elimu.launcher_custom.model.AppGroup;
import ai.elimu.model.gson.admin.ApplicationGson;

public class AppCollectionGenerator {

    // TODO: fetch via Appstore library
    public static AppCollection loadAppCollectionEnglish() {
        Log.i(AppCollectionGenerator.class.getName(), "loadAppCollectionEnglish");

        AppCollection appCollection = new AppCollection();

        

        return appCollection;
    }

    // TODO: fetch via Appstore library
    public static AppCollection loadAppCollectionSomali() {
        Log.i(AppCollectionGenerator.class.getName(), "loadAppCollectionSomali");

        AppCollection appCollection = new AppCollection();



        return appCollection;
    }

    private static AppGroup loadAppGroup(String... packageNames) {
        AppGroup appGroup = new AppGroup();

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
