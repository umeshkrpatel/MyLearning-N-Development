package com.github.umeshkrpatel.growthmonitor;

import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.umeshkrpatel.growthmonitor.data.ChartData;

import java.util.ArrayList;

public class GrowthChartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAG = "GrowthChartActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Spinner mXAxisBar, mYAxisBar;
    private Button mUpdate;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int mBabyId;
    private static ChartData.ChartType[][] dChart = new ChartData.ChartType[][] {
            {ChartData.ChartType.AGE, ChartData.ChartType.WEIGHT},
            {ChartData.ChartType.AGE, ChartData.ChartType.HEIGHT},
            {ChartData.ChartType.AGE, ChartData.ChartType.HEADCIRCUM},
    };

    private static Integer sFragmentCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth_chart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.chart_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.gcNavView);
        navigationView.setNavigationItemSelectedListener(this);

        mXAxisBar = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.sbXAxis);
        mXAxisBar.setOnItemSelectedListener(this);
        mXAxisBar.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_listview,
                R.id.tvSpinnerList, ChartData.mMonth));
        mXAxisBar.setSelection(ChartData.minRange());

        mYAxisBar = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.sbYAxis);
        mYAxisBar.setOnItemSelectedListener(this);
        mYAxisBar.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_listview,
                R.id.tvSpinnerList, ChartData.mMonth));
        mYAxisBar.setSelection(ChartData.maxRange());
        mUpdate = (Button) navigationView.getHeaderView(0).findViewById(R.id.nvBtnUpdate);
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChartData.setMinRange(mXAxisBar.getSelectedItemPosition());
                ChartData.setMaxRange(mYAxisBar.getSelectedItemPosition());
                Integer pos = mSectionsPagerAdapter.getCurrentPosition();
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

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.chart_drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mBabyId = BabysInfo.getCurrentBabyId();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_growth_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mXAxisBar) {
            if (position == (ChartData.mMonth.size() - 1)) {
                mXAxisBar.setSelection(position - 1);
                return;
            }
            if (position >= mYAxisBar.getSelectedItemPosition()) {
                mYAxisBar.setSelection(position + 1, true);
            }
        } else {
            if (position == 0) {
                mYAxisBar.setSelection(position + 1);
                return;
            }
            if (position <= mXAxisBar.getSelectedItemPosition()) {
                mXAxisBar.setSelection(position - 1, true);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<GrowthChartFragment> mf = new ArrayList<>();
        private int mCurrent = 0;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            for (int i = 0; i < sFragmentCount; i++) {
                mf.add(GrowthChartFragment.newInstance(dChart[i][0], dChart[i][1]));
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mf.get(position);
        }

        public Integer getCurrentPosition() {
            return mCurrent;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return sFragmentCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 3) {
                return "Check";
            }
            return dChart[position][0].toString() + "-" + dChart[position][1].toString();
        }
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            Log.d(TAG, "I am in right place or Not");
            mCurrent = position;
        }
    }
}
