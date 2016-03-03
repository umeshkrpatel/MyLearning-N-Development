package com.github.umeshkrpatel.growthmonitor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.github.umeshkrpatel.growthmonitor.data.ChartData;
import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.multispinner.MultiSpinner;
import com.github.umeshkrpatel.rangeselector.RangeSelector;

import java.util.ArrayList;

public class GrowthChartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MultiSpinner.MultiSpinnerListener {

    private static final String TAG = "GrowthChartActivity";

    @Nullable
    private static String[] sChartList = null;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    //private Spinner mXAxisBar, mYAxisBar;
    private RangeSelector mRangeSelector;
    private MultiSpinner msSpinner;

    @NonNull
    private static final ChartData.AxisType[] dChart = new ChartData.AxisType[] {
        ChartData.AxisType.WEIGHT, ChartData.AxisType.HEIGHT, ChartData.AxisType.HEADSIZE,
    };

    @NonNull
    private static final int[] mNameIDs = new int[] {
        R.string.weight, R.string.height, R.string.headc
    };

    private static final int sFragmentCount = 3;

    public static void createChartList(@NonNull Context context) {
        if (sChartList == null)
            sChartList = context.getResources().getStringArray(R.array.chartListItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_chart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createChartList(this);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.chart_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.gcNavView);
        navigationView.setNavigationItemSelectedListener(this);

//        msSpinner =
//            (MultiSpinner) navigationView.getHeaderView(0).findViewById(R.id.msAllBaby);
//        msSpinner.setAdapter(
//            new ArrayAdapter<>(
//                this, R.layout.spinner_listview, R.id.tvSpinnerList, IBabyInfo.getBabyInfoList()),
//            false, this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_growth_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.navRangeFilter:
                createRangeSelector();
                break;

            case R.id.navBabyCompare:
                createBabyCompareList();
                break;

            case R.id.navChartType:
                ChartData.toggleChartType();
                item.setIcon(ChartData.chartID());
                item.setTitle(ChartData.chartTitle());
                updateChart();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.chart_drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void onItemsSelected(boolean[] selected) {
        for (IBabyInfo info : IBabyInfo.getBabyInfoList()) {
            info.setActive(false);
        }
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                IBabyInfo info = (IBabyInfo) msSpinner.getAdapter().getItem(i);
                info.setActive(true);
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        @NonNull
        private ArrayList<GrowthChartFragment> mf = new ArrayList<>();
        private int mCurrent = 0;
        private final Context mContext;
        public SectionsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            for (int i = 0; i < sFragmentCount; i++) {
                mf.add(GrowthChartFragment.getOrCreate(
                    ChartData.AxisType.AGE, dChart[i])
                );
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mf.get(position);
        }

        public int getCurrentPosition() {
            return mCurrent;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return sFragmentCount;
        }

        @NonNull
        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(R.string.age) + "-"
                + mContext.getResources().getString(mNameIDs[position]);
        }
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            Log.d(TAG, "I am in right place or Not");
            mCurrent = position;
        }
    }

    private void updateChart() {
        int pos = mSectionsPagerAdapter.getCurrentPosition();
        GrowthChartFragment gf;
        if (pos != 1) {
            gf = (GrowthChartFragment) mSectionsPagerAdapter.getItem(pos);
            gf.updateChart();
            gf = (GrowthChartFragment) mSectionsPagerAdapter.getItem(1);
            gf.updateChart();
        } else {
            for (int i = 0; i < sFragmentCount; i++) {
                gf = (GrowthChartFragment) mSectionsPagerAdapter.getItem(i);
                gf.updateChart();
            }
        }
    }

    private void createRangeSelector() {
        if (sChartList == null) {
            return;
        }

        mRangeSelector =
          new RangeSelector(this, this)
            .setAdapter(
              new ArrayAdapter<>(
                this, R.layout.spinner_listview, R.id.tvSpinnerList, sChartList)
            );
        mRangeSelector.setTitle(android.R.string.dialog_alert_title)
          .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  ChartData.setMinRange(mRangeSelector.getMinRange());
                  ChartData.setMaxRange(mRangeSelector.getMaxRange());
                  updateChart();
              }
          })
          .setNegativeButton(R.string.no, null);
        mRangeSelector.show();
    }

    private void createBabyCompareList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View babyListView = inflater.inflate(R.layout.layout_multispineer, null);
        builder.setView(babyListView);
        msSpinner = (MultiSpinner) babyListView.findViewById(R.id.alertMultiSpinner);
        msSpinner.setAdapter(
          new ArrayAdapter<>(
            this, R.layout.spinner_listview, R.id.tvSpinnerList, IBabyInfo.getBabyInfoList()),
          false, this);
        builder.create().show();
    }
}
