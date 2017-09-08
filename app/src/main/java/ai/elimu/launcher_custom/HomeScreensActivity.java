package ai.elimu.launcher_custom;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.andraskindler.parallaxviewpager.ParallaxViewPager;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

import java.util.List;

import ai.elimu.analytics.eventtracker.EventTracker;
import ai.elimu.launcher_custom.model.AppCategory;
import ai.elimu.launcher_custom.model.AppCollection;
import ai.elimu.launcher_custom.model.AppGroup;
import ai.elimu.model.gson.admin.ApplicationGson;

public class HomeScreensActivity extends AppCompatActivity {

    private static AppCollection appCollection;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ParallaxViewPager viewPager;

    private DotIndicator dotIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate(savedInstanceState);

        if ("en".equals(BuildConfig.FLAVOR)) {
            appCollection = AppCollectionGenerator.loadAppCollectionEnglish();
        } else if ("so".equals(BuildConfig.FLAVOR)) {
            appCollection = AppCollectionGenerator.loadAppCollectionSomali();
        }
        Log.i(getClass().getName(), "appCollection.getAppCategories().size(): " + appCollection.getAppCategories().size());

        setContentView(ai.elimu.launcher_custom.R.layout.activity_home_screens);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ParallaxViewPager) findViewById(ai.elimu.launcher_custom.R.id.container);
        viewPager.setBackgroundResource(R.drawable.background_indigo);
        viewPager.setAdapter(mSectionsPagerAdapter);

        dotIndicator = (DotIndicator) findViewById(ai.elimu.launcher_custom.R.id.dotIndicator);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(getClass().getName(), "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                Log.i(getClass().getName(), "onPageSelected");

                dotIndicator.setSelectedItem(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i(getClass().getName(), "onPageScrollStateChanged");

            }
        });
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
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

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            Log.i(getClass().getName(), "sectionNumber: " + sectionNumber);

            View rootView = inflater.inflate(R.layout.fragment_home_screen, container, false);

            // Set category name
            TextView textViewCategoryName = (TextView) rootView.findViewById(R.id.textViewCategoryName);
            AppCategory appCategory = appCollection.getAppCategories().get(sectionNumber);
            textViewCategoryName.setText(appCategory.getName());

            LinearLayout linearLayoutAppGroupsContainer = (LinearLayout) rootView.findViewById(R.id.linearLayoutAppGroupsContainer);
            initializeAppCategory(linearLayoutAppGroupsContainer, sectionNumber);

            return rootView;
        }

        private void initializeAppCategory(LinearLayout linearLayoutAppGroupsContainer, int sectionNumber) {
            Log.i(getClass().getName(), "initializeAppCategory");

            final AppCategory appCategory = appCollection.getAppCategories().get(sectionNumber);
            Log.i(getClass().getName(), "appCategory.getName(): " + appCategory.getName());

            List<AppGroup> appGroups = appCategory.getAppGroups();
            Log.i(getClass().getName(), "appGroups.size(): " + appGroups.size());

            for (AppGroup appGroup : appGroups) {
                Log.i(getClass().getName(), "appGroup.getApplications().size(): " + appGroup.getApplications().size());

                FlowLayout flowLayoutAppGroup = (FlowLayout) LayoutInflater.from(getActivity())
                        .inflate(R.layout.fragment_home_screen_app_group, linearLayoutAppGroupsContainer, false);

                for (final ApplicationGson application : appGroup.getApplications()) {
                    Log.i(getClass().getName(), "application.getPackageName(): " + application.getPackageName());

                    LinearLayout linearLayoutAppView = (LinearLayout) LayoutInflater.from(getActivity())
                            .inflate(R.layout.fragment_home_screen_app_group_app_view, flowLayoutAppGroup, false);

                    final PackageManager packageManager = getActivity().getPackageManager();

                    // Set app icon
                    try {
                        Drawable icon = packageManager.getApplicationIcon(application.getPackageName());
                        Log.i(getClass().getName(), "icon: " + icon);
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

                        flowLayoutAppGroup.addView(linearLayoutAppView);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    // Set app title
                    try {
                        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(application.getPackageName(), 0);
                        String applicationLabel = packageManager.getApplicationLabel(applicationInfo).toString();
                        Log.i(getClass().getName(), "applicationLabel: " + applicationLabel);
                        TextView appLabelTextView = (TextView) linearLayoutAppView.findViewById(R.id.textViewAppLabel);
                        appLabelTextView.setText(applicationLabel);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                linearLayoutAppGroupsContainer.addView(flowLayoutAppGroup);
            }
        }

        @Override
        public void onStart() {
            Log.i(getClass().getName(), "onCreateView");
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
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return appCollection.getAppCategories().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return appCollection.getAppCategories().get(position).getName();
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(getClass().getName(), "onBackPressed");

        // Do nothing
    }
}
