package ai.elimu.launcher_custom;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andraskindler.parallaxviewpager.ParallaxViewPager;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

import java.io.File;
import java.util.List;

import ai.elimu.analytics.eventtracker.EventTracker;
import ai.elimu.model.gson.admin.ApplicationGson;
import ai.elimu.model.gson.project.AppCategoryGson;
import ai.elimu.model.gson.project.AppCollectionGson;
import ai.elimu.model.gson.project.AppGroupGson;

public class HomeScreensActivity extends AppCompatActivity {

    private static AppCollectionGson appCollection;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ParallaxViewPager viewPager;

    private DotIndicator dotIndicator;

    private static int currentPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        // The Appstore app should store an "app-collection.json" file when the Applications downloaded belong to a Project's AppCollection
        File jsonFile = new File(Environment.getExternalStorageDirectory() + "/.elimu-ai/appstore/", "app-collection.json");
        Log.i(AppCollectionGenerator.class.getName(), "jsonFile: " + jsonFile);
        Log.i(AppCollectionGenerator.class.getName(), "jsonFile.exists(): " + jsonFile.exists());
        appCollection = AppCollectionGenerator.loadAppCollectionFromJsonFile(jsonFile);
        Log.i(getClass().getName(), "appCollection.getAppCategories().size(): " + appCollection.getAppCategories().size());

        setContentView(ai.elimu.launcher_custom.R.layout.activity_home_screens);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        dotIndicator = (DotIndicator) findViewById(ai.elimu.launcher_custom.R.id.dotIndicator);
        dotIndicator.setNumberOfItems(appCollection.getAppCategories().size());

        Log.i(getClass().getName(), "onCreate currentPosition: " + currentPosition);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ParallaxViewPager) findViewById(ai.elimu.launcher_custom.R.id.container);
        viewPager.setBackgroundResource(R.drawable.background_indigo);
        viewPager.setAdapter(mSectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.i(getClass().getName(), "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(getClass().getName(), "onPageSelected");
                Log.i(getClass().getName(), "position: " + position);

                dotIndicator.setSelectedItem(position, true);
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(getClass().getName(), "onPageScrollStateChanged");

            }
        });
    }

    @Override
    protected void onStart() {
        Log.i(getClass().getName(), "onStart");
        super.onStart();

        Log.i(getClass().getName(), "onStart currentPosition: " + currentPosition);
    }

    @Override
    protected void onResume() {
        Log.i(getClass().getName(), "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(getClass().getName(), "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(getClass().getName(), "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(getClass().getName(), "onDestroy");
        super.onDestroy();
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            Log.i(PlaceholderFragment.class.getName(), "newInstance");
            Log.i(PlaceholderFragment.class.getName(), "newInstance sectionNumber: " + sectionNumber);
            Log.i(PlaceholderFragment.class.getName(), "newInstance currentPosition: " + currentPosition);

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Log.i(getClass().getName(), "onCreateView");

            Log.i(getClass().getName(), "onCreateView currentPosition: " + currentPosition);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.i(getClass().getName(), "onCreateView sectionNumber: " + sectionNumber);
//            if ((sectionNumber == 0) && (currentPosition > 0)) {
//                sectionNumber = currentPosition;
//            }
//            Log.i(getClass().getName(), "onCreateView sectionNumber: " + sectionNumber);

            View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);

            // Set category name
            TextView textViewCategoryName = (TextView) rootView.findViewById(R.id.textViewCategoryName);
            AppCategoryGson appCategory = appCollection.getAppCategories().get(sectionNumber);
            textViewCategoryName.setText(appCategory.getName());

            LinearLayout linearLayoutAppGroupsContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutAppGroupsContainer);
            initializeAppCategory(linearLayoutAppGroupsContainer, sectionNumber);

            return rootView;
        }

        private void initializeAppCategory(LinearLayout linearLayoutAppGroupsContainer, int sectionNumber) {
            Log.i(getClass().getName(), "initializeAppCategory");

            Log.i(getClass().getName(), "initializeAppCategory sectionNumber: " + sectionNumber);

            final AppCategoryGson appCategory = appCollection.getAppCategories().get(sectionNumber);
            Log.i(getClass().getName(), "initializeAppCategory appCategory.getName(): " + appCategory.getName());

            List<AppGroupGson> appGroups = appCategory.getAppGroups();
//            Log.i(getClass().getName(), "appGroups.size(): " + appGroups.size());

            for (AppGroupGson appGroup : appGroups) {
//                Log.i(getClass().getName(), "appGroup.getApplications().size(): " + appGroup.getApplications().size());

                FlowLayout flowLayoutAppGroup = (FlowLayout) LayoutInflater.from(getActivity())
                        .inflate(R.layout.fragment_home_screen_app_group, linearLayoutAppGroupsContainer, false);

                for (final ApplicationGson application : appGroup.getApplications()) {
//                    Log.i(getClass().getName(), "application.getPackageName(): " + application.getPackageName());

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
//                        Log.i(getClass().getName(), "icon: " + icon);
                        LinearLayout linearLayoutAppView = (LinearLayout) LayoutInflater.from(getActivity())
                                .inflate(R.layout.fragment_home_screen_app_group_app_view, flowLayoutAppGroup, false);
                        ImageView appIconImageView = (ImageView) linearLayoutAppView.findViewById(ai.elimu.launcher_custom.R.id.appIconImageView);
                        appIconImageView.setImageDrawable(icon);
                        appIconImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i(getClass().getName(), "appIconImageView onClick");

                                Intent intent = packageManager.getLaunchIntentForPackage(application.getPackageName());
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                startActivity(intent);

                                EventTracker.reportApplicationOpenedEvent(getContext(), application.getPackageName());
                            }
                        });

                        // Set app title
                        String applicationLabel = packageManager.getApplicationLabel(applicationInfo).toString();
//                        Log.i(getClass().getName(), "applicationLabel: " + applicationLabel);
                        TextView appLabelTextView = (TextView) linearLayoutAppView.findViewById(R.id.textViewAppLabel);
                        appLabelTextView.setText(applicationLabel);

                        flowLayoutAppGroup.addView(linearLayoutAppView);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(getClass().getName(), "Application not installed: " + application.getPackageName());
                    }
                }

                if (flowLayoutAppGroup.getChildCount() > 0) {
                    linearLayoutAppGroupsContainer.addView(flowLayoutAppGroup);
                }
            }
        }

        @Override
        public void onStart() {
            Log.i(getClass().getName(), "onStart");
            super.onStart();
        }

        @Override
        public void onResume() {
            Log.i(getClass().getName(), "onResume");
            super.onResume();
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i(getClass().getName(), "getItem");
            Log.i(getClass().getName(), "getItem position: " + position);
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            Log.i(getClass().getName(), "getCount");
            return appCollection.getAppCategories().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.i(getClass().getName(), "getPageTitle");
            return appCollection.getAppCategories().get(position).getName();
        }
    }


    @Override
    public void onBackPressed() {
        Log.i(getClass().getName(), "onBackPressed");

        // Do nothing
    }
}
