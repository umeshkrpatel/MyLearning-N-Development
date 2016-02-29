package com.github.umeshkrpatel.growthmonitor;

import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.umeshkrpatel.growthmonitor.data.ChartData;
import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;
import com.github.umeshkrpatel.multispinner.MultiSpinner;

import java.util.ArrayList;

public class GrowthChartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener, MultiSpinner.MultiSpinnerListener {

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
    private ViewPager viewPager;
    private Spinner mXAxisBar, mYAxisBar;
    private MultiSpinner msSpinner;

    @NonNull
    private static final ChartData.ChartType[] dChart = new ChartData.ChartType[] {
        ChartData.ChartType.WEIGHT, ChartData.ChartType.HEIGHT, ChartData.ChartType.HEADCIRCUM,
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.chart_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.gcNavView);
        navigationView.setNavigationItemSelectedListener(this);

        mXAxisBar = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.sbXAxis);
        mXAxisBar.setOnItemSelectedListener(this);
        assert sChartList != null;
        mXAxisBar.setAdapter(
            new ArrayAdapter<>(this, R.layout.spinner_listview, R.id.tvSpinnerList, sChartList));
        mXAxisBar.setSelection(ChartData.minRange());

        mYAxisBar = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.sbYAxis);
        mYAxisBar.setOnItemSelectedListener(this);
        mYAxisBar.setAdapter(
            new ArrayAdapter<>(this, R.layout.spinner_listview, R.id.tvSpinnerList, sChartList));
        mYAxisBar.setSelection(ChartData.maxRange());

        msSpinner =
            (MultiSpinner) navigationView.getHeaderView(0).findViewById(R.id.msAllBaby);
        msSpinner.setAdapter(
            new ArrayAdapter<IBabyInfo>(
                this, R.layout.spinner_listview, R.id.tvSpinnerList, IBabyInfo.getBabyInfoList()),
            false, this);

        Button btnUpdate = (Button) navigationView.getHeaderView(0).findViewById(R.id.nvBtnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChartData.setMinRange(mXAxisBar.getSelectedItemPosition());
                ChartData.setMaxRange(mYAxisBar.getSelectedItemPosition());
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

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.chart_drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
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

        handleIntent(savedInstanceState);
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
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mXAxisBar) {
            assert sChartList != null;
            if (position == (sChartList.length - 1)) {
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
                mf.add(GrowthChartFragment.newInstance(ChartData.ChartType.AGE, dChart[i]));
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

    void handleIntent(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                int actionType = bundle.getInt(IDataInfo.ACTION_TYPE);
                if (actionType == IDataInfo.ACTION_UPDATE) {
                    int actionEvent = bundle.getInt(IDataInfo.ACTION_EVENT);
                    int actionValue = bundle.getInt(IDataInfo.ACTION_VALUE);
                    if (actionEvent == IDataInfo.EVENT_LIFEEVENT) {
                        viewPager.setCurrentItem(actionEvent);
                    }
                }
            }
        }
    }
}
