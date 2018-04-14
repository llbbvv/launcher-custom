package ai.elimu.launcher_custom;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Fetch Appstore version
        try {
            PackageInfo packageInfoAppstore = getPackageManager().getPackageInfo(BuildConfig.APPSTORE_APPLICATION_ID, 0);
            Log.i(getClass().getName(), "packageInfoAppstore.versionCode: " + packageInfoAppstore.versionCode);
            // TODO: match available ContentProvider queries with the Appstore's versionCode
        } catch (PackageManager.NameNotFoundException e) {
            // The Appstore app has not been installed
            Log.w(getClass().getName(), null, e);
            Toast.makeText(getApplicationContext(), "This launcher will not work until you install the Appstore app: " + BuildConfig.APPSTORE_APPLICATION_ID, Toast.LENGTH_LONG).show();
            // TODO: force the user to install the Appstore app
        }

        // Ask for read permission (needed for getting AppCollection from SD card)
        int permissionCheckReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Timber.i("permissionCheckReadExternalStorage: " + permissionCheckReadExternalStorage);
        if (permissionCheckReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            Timber.i("permissionCheckReadExternalStorage != PackageManager.PERMISSION_GRANTED");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> sharedPreferencesMapKeySet = sharedPreferences.getAll().keySet();
        Timber.i("sharedPreferencesMapKeySet: " + sharedPreferencesMapKeySet);
        // TODO: if empty (first-time launch), redirect user to the SettingsActivity

        launchHomeScreensActivity();
    }

    private void launchHomeScreensActivity() {
        Timber.i("launchHomeScreensActivity");

        Intent intent = new Intent(getApplicationContext(), HomeScreensActivity.class);
        startActivity(intent);

        // Requires root
//        Intent serviceIntent = new Intent(this, StatusBarService.class);
//        startService(serviceIntent);

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Timber.i("onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Timber.i("Permission granted");

                launchHomeScreensActivity();
            } else {
                Timber.w("Permission denied");

                Toast.makeText(getApplicationContext(), "Permission denied: READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();

                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Timber.i("onBackPressed");
        // Do nothing
    }
}
