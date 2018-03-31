package ai.elimu.launcher_custom.settings;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

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
     * This fragment shows AppCategory preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AppCategoriesPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            Timber.i("AppCategoriesPreferenceFragment onCreate");
            super.onCreate(savedInstanceState);

//            addPreferencesFromResource(R.xml.pref_app_categories);
            // Create one SwitchPreference per AppCategory
            PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
            for (AppCategoryGson appCategory : appCategories) {
                String preferenceKey = PreferenceKeyHelper.getPreferenceKey(appCategory);

                String preferenceTitle = appCategory.getName();
                Timber.i("preferenceTitle: " + preferenceTitle);

                SwitchPreference switchPreference = new SwitchPreference(getActivity());
                switchPreference.setKey(preferenceKey);
                switchPreference.setTitle(preferenceTitle);
                switchPreference.setDefaultValue(true);

                preferenceScreen.addPreference(switchPreference);
            }
            setPreferenceScreen(preferenceScreen);

            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Timber.i("AppCategoriesPreferenceFragment onOptionsItemSelected");

            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
