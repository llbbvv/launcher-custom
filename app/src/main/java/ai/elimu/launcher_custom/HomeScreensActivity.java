package ai.elimu.launcher_custom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andraskindler.parallaxviewpager.ParallaxViewPager;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ai.elimu.analytics.eventtracker.EventTracker;
import ai.elimu.launcher_custom.util.PreferenceKeyHelper;
import ai.elimu.model.gson.admin.ApplicationGson;
import ai.elimu.model.gson.project.AppCategoryGson;
import ai.elimu.model.gson.project.AppCollectionGson;
import ai.elimu.model.gson.project.AppGroupGson;
import timber.log.Timber;

public class HomeScreensActivity extends AppCompatActivity {

    private static List<AppCategoryGson> appCategories;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ParallaxViewPager viewPager;

    private DotIndicator dotIndicator;

    private static int currentPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i( "onCreate");
        super.onCreate(savedInstanceState);

        // The Appstore app should store an "app-collection.json" file when the Applications downloaded belong to a Project's AppCollection
        // TODO: replace this with ContentProvider solution
        File jsonFile = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/", "app-collection.json");
        Timber.i("jsonFile: " + jsonFile);
        Timber.i("jsonFile.exists(): " + jsonFile.exists());
        AppCollectionGson appCollection = AppCollectionGenerator.loadAppCollectionFromJsonFile(jsonFile);
        Timber.i("appCollection.getAppCategories().size(): " + appCollection.getAppCategories().size());

        // Hide AppCategories that have been un-checked in the SettingsActivity
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        appCategories = new ArrayList<>();
        for (AppCategoryGson appCategory : appCollection.getAppCategories()) {
            String preferenceKey = PreferenceKeyHelper.getPreferenceKey(appCategory);
            Timber.i("preferenceKey: " + preferenceKey);
            boolean isAppCategoryHidden = !sharedPreferences.getBoolean(preferenceKey, true);
            Timber.i("isAppCategoryHidden: " + isAppCategoryHidden);
            if (!isAppCategoryHidden) {
                appCategories.add(appCategory);
            }
        }

        setContentView(ai.elimu.launcher_custom.R.layout.activity_home_screens);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        dotIndicator = (DotIndicator) findViewById(ai.elimu.launcher_custom.R.id.dotIndicator);
        dotIndicator.setNumberOfItems(appCategories.size());

        Timber.i( "onCreate currentPosition: " + currentPosition);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ParallaxViewPager) findViewById(ai.elimu.launcher_custom.R.id.container);
        viewPager.setBackgroundResource(R.drawable.background_indigo);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Timber.i( "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                Timber.i( "onPageSelected");
                Timber.i( "position: " + position);

                dotIndicator.setSelectedItem(position, true);
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Timber.i("onPageScrollStateChanged");
            }
        });
    }

    @Override
    protected void onStart() {
        Timber.i("onStart");
        super.onStart();

        Timber.i("onStart currentPosition: " + currentPosition);
    }

    @Override
    protected void onResume() {
        Timber.i("onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Timber.i("onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Timber.i("onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Timber.i("onDestroy");
        super.onDestroy();
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            Timber.i("newInstance");
            Timber.i("newInstance sectionNumber: " + sectionNumber);
            Timber.i("newInstance currentPosition: " + currentPosition);

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Timber.i("onCreateView");

            Timber.i("onCreateView currentPosition: " + currentPosition);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            Timber.i("onCreateView sectionNumber: " + sectionNumber);
//            if ((sectionNumber == 0) && (currentPosition > 0)) {
//                sectionNumber = currentPosition;
//            }
//            Timber.i( "onCreateView sectionNumber: " + sectionNumber);

            View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);

            // Set category name
            TextView textViewCategoryName = (TextView) rootView.findViewById(R.id.textViewCategoryName);
            AppCategoryGson appCategory = appCategories.get(sectionNumber);
            textViewCategoryName.setText(appCategory.getName());

            LinearLayout linearLayoutAppGroupsContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutAppGroupsContainer);
            initializeAppCategory(linearLayoutAppGroupsContainer, sectionNumber);

            return rootView;
        }

        private void initializeAppCategory(LinearLayout linearLayoutAppGroupsContainer, int sectionNumber) {
            Timber.i( "initializeAppCategory");

            Timber.i("initializeAppCategory sectionNumber: " + sectionNumber);

            final AppCategoryGson appCategory = appCategories.get(sectionNumber);
            Timber.i("initializeAppCategory appCategory.getName(): " + appCategory.getName());

            List<AppGroupGson> appGroups = appCategory.getAppGroups();
//            Timber.i( "appGroups.size(): " + appGroups.size());

            for (AppGroupGson appGroup : appGroups) {
//                Timber.i("appGroup.getApplications().size(): " + appGroup.getApplications().size());

                FlowLayout flowLayoutAppGroup = (FlowLayout) LayoutInflater.from(getActivity())
                        .inflate(R.layout.fragment_home_screen_app_group, linearLayoutAppGroupsContainer, false);

                for (final ApplicationGson application : appGroup.getApplications()) {
//                    Timber.i("application.getPackageName(): " + application.getPackageName());

                    final PackageManager packageManager = getActivity().getPackageManager();

                    try {
                        // Set app icon
                        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
                        Resources resources = packageManager.getResourcesForApplication(application.getPackageName());
                        Drawable icon;
                        if (Build.VERSION.SDK_INT > 21) {
                            icon = resources.getDrawableForDensity(applicationInfo.icon, DisplayMetrics.DENSITY_XXHIGH, null);
                        } else {
                            //This method was deprecated in API level 22
                            icon = resources.getDrawableForDensity(applicationInfo.icon, DisplayMetrics.DENSITY_XXHIGH);
                        }
//                        Timber.i("icon: " + icon);
                        LinearLayout linearLayoutAppView = (LinearLayout) LayoutInflater.from(getActivity())
                                .inflate(R.layout.fragment_home_screen_app_group_app_view, flowLayoutAppGroup, false);
                        ImageView appIconImageView = (ImageView) linearLayoutAppView.findViewById(ai.elimu.launcher_custom.R.id.appIconImageView);
                        appIconImageView.setImageDrawable(icon);
                        appIconImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Timber.i("appIconImageView onClick");

                                Intent intent = packageManager.getLaunchIntentForPackage(application.getPackageName());
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                startActivity(intent);

                                EventTracker.reportApplicationOpenedEvent(getContext(), application.getPackageName());
                            }
                        });

                        // Set app title
                        String applicationLabel = packageManager.getApplicationLabel(applicationInfo).toString();
//                        Timber.i("applicationLabel: " + applicationLabel);
                        TextView appLabelTextView = (TextView) linearLayoutAppView.findViewById(R.id.textViewAppLabel);
                        appLabelTextView.setText(applicationLabel);

                        flowLayoutAppGroup.addView(linearLayoutAppView);
                    } catch (PackageManager.NameNotFoundException e) {
                        Timber.e("Application not installed: " + application.getPackageName());
                    }
                }

                if (flowLayoutAppGroup.getChildCount() > 0) {
                    linearLayoutAppGroupsContainer.addView(flowLayoutAppGroup);
                }
            }
        }

        @Override
        public void onStart() {
            Timber.i("onStart");
            super.onStart();
        }

        @Override
        public void onResume() {
            Timber.i( "onResume");
            super.onResume();
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Timber.i("getItem");
            Timber.i("getItem position: " + position);
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
//            Timber.i("getCount");
            return appCategories.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Timber.i("getPageTitle");
            return appCategories.get(position).getName();
        }
    }


    @Override
    public void onBackPressed() {
        Timber.i("onBackPressed");

        // Do nothing
    }
}
