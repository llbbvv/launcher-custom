package ai.elimu.launcher_custom;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Log config
        Timber.plant(new Timber.DebugTree());
        Timber.i("onCreate");

        if ("release".equals(BuildConfig.BUILD_TYPE)) {
            // Initialize Crashlytics (crash reporting)
            Fabric.with(this, new Crashlytics());
        }
    }
}
