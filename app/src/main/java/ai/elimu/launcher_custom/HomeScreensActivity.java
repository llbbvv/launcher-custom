package ai.elimu.launcher_custom;

import android.animation.ArgbEvaluator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.matthewtamlin.sliding_intro_screen_library.indicators.DotIndicator;

import ai.elimu.analytics.eventtracker.EventTracker;
import ai.elimu.model.enums.content.LiteracySkill;
import ai.elimu.model.enums.content.NumeracySkill;

import java.util.Arrays;
import java.util.List;

public class HomeScreensActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager viewPager;

    private DotIndicator dotIndicator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ai.elimu.launcher_custom.R.layout.activity_home_screens);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(ai.elimu.launcher_custom.R.id.container);
//        viewPager.setBackgroundResource(ai.elimu.launcher_custom.R.drawable.background);
        viewPager.setBackgroundColor(getResources().getColor(R.color.purple));
        viewPager.setAdapter(mSectionsPagerAdapter);

        dotIndicator = (DotIndicator) findViewById(ai.elimu.launcher_custom.R.id.dotIndicator);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i(getClass().getName(), "onPageScrolled");

                Integer color1 = getResources().getColor(R.color.purple);
                Integer color2 = getResources().getColor(R.color.deep_purple);
                Integer color3 = getResources().getColor(R.color.indigo);
                Integer color4 = getResources().getColor(R.color.blue);

                Integer[] colors = {color1, color2, color3, color4};

                if(position < (mSectionsPagerAdapter.getCount() -1) && position < (colors.length - 1)) {
                    viewPager.setBackgroundColor((Integer) new ArgbEvaluator().evaluate(positionOffset, colors[position], colors[position + 1]));

                    float[] hsv = new float[3];
                    int darkerColor = (Integer) new ArgbEvaluator().evaluate(positionOffset, colors[position], colors[position + 1]);
                    Color.colorToHSV(darkerColor, hsv);
                    hsv[2] *= 0.8f; // value component
                    darkerColor = Color.HSVToColor(hsv);

                    Window window = getWindow();
                    window.setStatusBarColor(darkerColor);
                } else {
                    // the last page color
                    viewPager.setBackgroundColor(colors[colors.length - 1]);

                    float[] hsv = new float[3];
                    int darkerColor = (Integer) new ArgbEvaluator().evaluate(positionOffset, colors[position], colors[position + 1]);
                    Color.colorToHSV(darkerColor, hsv);
                    hsv[2] *= 0.8f; // value component
                    darkerColor = Color.HSVToColor(hsv);

                    Window window = getWindow();
                    window.setStatusBarColor(darkerColor);
                }
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

            int layoutIdentifier = getResources().getIdentifier("fragment_home_screen" + String.valueOf(sectionNumber), "layout", getActivity().getPackageName());
            View rootView = inflater.inflate(layoutIdentifier, container, false);

            if (sectionNumber == 1) {
                // 0. Logic

                GridLayout appGridLayout1 = (GridLayout) rootView.findViewById(ai.elimu.launcher_custom.R.id.appGridLayout1);
                List<String> packageNames = Arrays.asList(
                    // TODO
                );
                initializeCategory(packageNames, appGridLayout1);

                GridLayout appGridLayout2 = (GridLayout) rootView.findViewById(ai.elimu.launcher_custom.R.id.appGridLayout2);
                packageNames = Arrays.asList(
                    // TODO
                );
                initializeCategory(packageNames, appGridLayout2);

            } else if (sectionNumber == 2) {
                // 1. Sounds

                // TODO
            } else if (sectionNumber == 3) {
                // 2. Stories

                // TODO
            }  else if (sectionNumber == 4) {
                // 3. Letter shapes

                // TODO
            }

            return rootView;
        }

        private void initializeCategory(List<String> packageNames, GridLayout appGridLayout) {
            Log.i(getClass().getName(), "initializeCategory");

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            final PackageManager packageManager = getActivity().getPackageManager();
            List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(intent, 0);
            for (ResolveInfo resolveInfo : availableActivities) {
                final ActivityInfo activityInfo = resolveInfo.activityInfo;
                Log.i(getClass().getName(), "activityInfo: " + activityInfo);

                Log.i(getClass().getName(), "activityInfo.packageName: " + activityInfo.packageName);
                Log.i(getClass().getName(), "activityInfo.name: " + activityInfo.name);

                final ComponentName componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
                Log.i(getClass().getName(), "componentName: " + componentName);

                CharSequence label = resolveInfo.loadLabel(packageManager);
                Log.i(getClass().getName(), "label: " + label);

                Drawable icon = resolveInfo.loadIcon(packageManager);
                Log.i(getClass().getName(), "icon: " + icon);

                if (packageNames.contains(activityInfo.packageName)) {
                    View appView = LayoutInflater.from(getActivity()).inflate(ai.elimu.launcher_custom.R.layout.dialog_apps_app_view, appGridLayout, false);

                    ImageView appIconImageView = (ImageView) appView.findViewById(ai.elimu.launcher_custom.R.id.appIconImageView);
                    appIconImageView.setImageDrawable(icon);

                    appIconImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(getClass().getName(), "appIconImageView onClick");

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                            intent.setComponent(componentName);
                            startActivity(intent);

                            EventTracker.reportApplicationOpenedEvent(getContext(), activityInfo.packageName);
                        }
                    });

                    Log.i(getClass().getName(), "appGridLayout: " + appGridLayout);
                    Log.i(getClass().getName(), "appView: " + appView);
                    appGridLayout.addView(appView);
                }
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
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(getClass().getName(), "onBackPressed");

        // Do nothing
    }
}
