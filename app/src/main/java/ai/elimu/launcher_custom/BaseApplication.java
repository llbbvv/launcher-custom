package ai.elimu.launcher_custom;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Log config
        if (BuildConfig.DEBUG) {
            // Log everything
            Timber.plant(new Timber.DebugTree());
        } else {
            // Only log warnings and errors
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable throwable) {
                    if (priority == Log.WARN) {
                        Log.w(tag, message);
                    } else if (priority == Log.ERROR) {
                        Log.e(tag, message);
                    }
                }
            });
        }
        Timber.i("onCreate");

        if ("release".equals(BuildConfig.BUILD_TYPE)) {
            // Initialize Crashlytics (crash reporting)
            Fabric.with(this, new Crashlytics());
        }
    }
}
