package ai.elimu.launcher_custom.settings;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.util.List;

import ai.elimu.launcher_custom.AppCollectionGenerator;
import ai.elimu.launcher_custom.R;
import ai.elimu.launcher_custom.util.PreferenceKeyHelper;
import ai.elimu.model.gson.project.AppCategoryGson;
import ai.elimu.model.gson.project.AppCollectionGson;
import timber.log.Timber;

import static ai.elimu.launcher_custom.MainActivity.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static List<AppCategoryGson> appCategories;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        // Ask for read permission (needed for getting AppCollection from SD card)
        int permissionCheckReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheckReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }

        // The Appstore app should store an "app-collection.json" file when the Applications downloaded belong to a Project's AppCollection
        // TODO: replace this with ContentProvider solution
        File jsonFile = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/", "app-collection.json");
        Log.i(AppCollectionGenerator.class.getName(), "jsonFile: " + jsonFile);
        Log.i(AppCollectionGenerator.class.getName(), "jsonFile.exists(): " + jsonFile.exists());
        AppCollectionGson appCollection = AppCollectionGenerator.loadAppCollectionFromJsonFile(jsonFile);
        Log.i(getClass().getName(), "appCollection.getAppCategories().size(): " + appCollection.getAppCategories().size());
        appCategories = appCollection.getAppCategories();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Timber.i("onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted

                // Restart application
                Intent intent = getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                // Permission denied

                finish();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        Timber.i("onIsMultiPane");
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        Timber.i("onBuildHeaders");
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || AppCategoriesPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows AppCategory preferences only.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AppCategoriesPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            Timber.i("AppCategoriesPreferenceFragment onCreate");
            super.onCreate(savedInstanceState);

            // Check if the user has previously marked one or more AppCategories to be hidden
            boolean isOneOrMoreAppCategoriesHidden = false;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            for (AppCategoryGson appCategory : appCategories) {
                String preferenceKey = PreferenceKeyHelper.getPreferenceKey(appCategory);
                Timber.i("preferenceKey: " + preferenceKey);
                Timber.i("sharedPreferences.contains(preferenceKey): " + sharedPreferences.contains(preferenceKey));
                if (sharedPreferences.contains(preferenceKey)) {
                    boolean isAppCategoryHidden = !sharedPreferences.getBoolean(preferenceKey, true);
                    if (!isAppCategoryHidden) {
                        isOneOrMoreAppCategoriesHidden = true;
                    }
                }
            }
            Timber.i("isOneOrMoreAppCategoriesHidden: " + isOneOrMoreAppCategoriesHidden);

            // Create one SwitchPreference per AppCategory
            PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
            for (AppCategoryGson appCategory : appCategories) {
                String preferenceKey = PreferenceKeyHelper.getPreferenceKey(appCategory);
                Timber.i("preferenceKey: " + preferenceKey);

                String preferenceTitle = appCategory.getName();
                Timber.i("preferenceTitle: " + preferenceTitle);

                SwitchPreference switchPreference = new SwitchPreference(getActivity());
                switchPreference.setKey(preferenceKey);
                switchPreference.setTitle(preferenceTitle);
                if (isOneOrMoreAppCategoriesHidden) {
                    // To prevent automatic addition of new AppCategories added in the future, set default value to false
                    // if the user has already marked one or more AppCategories to be excluded. Typically, if the user
                    // has chosen to only display one AppCategory, we don't want new AppCategories to be automatically
                    // added to the Launcher once they are added to the website.
                    switchPreference.setDefaultValue(false);
                } else {
                    switchPreference.setDefaultValue(true);
                }

                preferenceScreen.addPreference(switchPreference);
            }
            setPreferenceScreen(preferenceScreen);
        }
    }
}
