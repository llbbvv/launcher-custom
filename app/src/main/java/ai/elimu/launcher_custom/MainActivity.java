package ai.elimu.launcher_custom;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;
    public static final int PERMISSION_REQUEST_READ_APP_COLLECTION_PROVIDER = 1;

    private static final String PERMISSION_READ_APP_COLLECTION_PROVIDER = "ai.elimu.appstore.provider.READ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Ask for read permission (needed for getting AppCollection from SD card)
        int permissionCheckReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheckReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }

        // Check for valid license
        Uri uri = Uri.parse("content://ai.elimu.appstore.provider/appCollection");
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null){
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                int columnAppCollectionId = cursor.getColumnIndex("appCollectionId");
                String appCollectionId = cursor.getString(columnAppCollectionId);
                Toast.makeText(getApplicationContext(), "AppCollectionId: " + appCollectionId, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), HomeScreensActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "No valid license found. Please validate your license first in the appstore.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "The appstore is not installed. Please install the appstore and validate your license.", Toast.LENGTH_LONG).show();
        }

        // Requires root
//        Intent serviceIntent = new Intent(this, StatusBarService.class);
//        startService(serviceIntent);

        finish();
    }

    @Override
    public void onBackPressed() {
        //do nothing
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
}
