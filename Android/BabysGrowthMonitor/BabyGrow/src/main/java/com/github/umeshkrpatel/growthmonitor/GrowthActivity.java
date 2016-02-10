package com.github.umeshkrpatel.growthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.umeshkrpatel.growthmonitor.data.GrowthDataProvider;
import com.github.umeshkrpatel.growthmonitor.data.IAdapter;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;
import com.github.umeshkrpatel.growthmonitor.data.VaccineScheduler;
import com.github.umeshkrpatel.growthmonitor.prefs.Preferences;

import java.util.ArrayList;

public class GrowthActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GrowthFragment.OnListFragmentInteractionListener {

    private ImageView ivNvBabyPic, ivNvBabyPicExtra;
    private GridLayout glDetails;
    private TextView tvNvBabyName;
    private int mBabyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_growth);

        initialize();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.gcNavView);
        navigationView.setNavigationItemSelectedListener(this);

        glDetails = (GridLayout) findViewById(R.id.glDetails);
        ivNvBabyPic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgBabyIcon);
        ivNvBabyPicExtra = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgBabyIconExt);
        tvNvBabyName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvBabyName);
        ivNvBabyPicExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BabiesInfo.setToNextIndex();
                updateMainView();
                Integer babyId = BabiesInfo.getCurrentBabyId();
                EventsInfo info = EventsInfo.get(babyId);
                ArrayList<EventsInfo.EventItem> eventItems = info.getList();
                IAdapter adapter = new TimlineAdapter(eventItems, null);
                GrowthFragment.get().setAdapter(adapter);
            }
        });

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        int Index = BabiesInfo.getCurrentIndex();
        if (BabiesInfo.size() == 0 && Index > 0) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        } else {
            updateMainView();
            EventsInfo.create(mBabyId);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        updateMainView();
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.growth, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_growth) {
            Intent intent = new Intent(this, GrowthChartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_babies) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_babies_list) {
            IAdapter adapter = new BabiesAdapter(BabiesInfo.getBabyInfoList(), this);
            GrowthFragment.get().setAdapter(adapter);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onEventInfoInteraction(EventsInfo.EventItem item) {

    }

    @Override
    public void onBabyInfoInteraction(BabiesInfo.BabyInfo item) {
        Intent intent = new Intent(this, GrowthChartActivity.class);
        intent.putExtra(GrowthChartActivity.ACTION_TYPE, IDataInfo.ACTION_UPDATE);
        intent.putExtra(GrowthChartActivity.ACTION_VALUE1, IDataInfo.EVENT_LIFEEVENT);
        intent.putExtra(GrowthChartActivity.ACTION_VALUE2, item.getId());
        startActivity(intent);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a GrowthChartFragment (defined as a static inner class below).
            return GrowthFragment.get();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    private void updateMainView() {
        BabiesInfo babiesInfo = BabiesInfo.get();
        Integer Index = BabiesInfo.getCurrentIndex();
        TextView tvBabyName = (TextView) findViewById(R.id.cgBabyName);
        TextView tvBabyDOB = (TextView) findViewById(R.id.cgBabyDob);
        ImageView ivBabyPicture = (ImageView) findViewById(R.id.cgBabyView);
        mBabyId = babiesInfo.getBabyInfoId(Index);
        Long dob = babiesInfo.getBabyInfoDob(Index);
        String gen = babiesInfo.getBabyInfoGender(Index);
        tvBabyName.setText(babiesInfo.getBabyInfoName(Index));
        tvNvBabyName.setText(babiesInfo.getBabyInfoName(Index));
        tvBabyDOB.setText(Utility.getDateTimeFromMillisecond(dob));
        int genId = gen.equals("Girl")?1:0;
        float age = Utility.fromMiliSecondsToMonths(System.currentTimeMillis() - dob);
        if (gen.equals("Girl")) {
            glDetails.setBackgroundResource(R.drawable.sg_bg_round_pink);
        } else {
            glDetails.setBackgroundResource(R.drawable.sg_bg_round_blue);
        }
        int imgR = BabiesInfo.getBabyImage(genId, age);
        ivBabyPicture.setImageResource(imgR);
        ivNvBabyPic.setImageResource(imgR);
        if (babiesInfo.getBabyInfoCount() > 1) {
            ivNvBabyPicExtra.setVisibility(View.VISIBLE);
            Index = (Index + 1) % BabiesInfo.size();
            dob = babiesInfo.getBabyInfoDob(Index);
            gen = babiesInfo.getBabyInfoGender(Index);
            genId = gen.equals("Girl")?1:0;
            age = Utility.fromMiliSecondsToMonths(System.currentTimeMillis() - dob);
            imgR = BabiesInfo.getBabyImage(genId, age);
            ivNvBabyPicExtra.setImageResource(imgR);
        } else {
            ivNvBabyPicExtra.setVisibility(View.GONE);
        }
    }

    public void finish() {
        Preferences.saveValue(Preferences.kCurrentBabyID, BabiesInfo.getCurrentIndex());
    }

    private void initialize() {
        Preferences.create(getPreferences(MODE_PRIVATE));

        BabiesInfo.setCurrentIndex(
                Preferences.readValue(Preferences.kCurrentBabyID, Preferences.kDefCurrentBabyID));

        ResourceReader.create(this);

        GrowthDataProvider.create(this);
        VaccineScheduler.create(this);
        BabiesInfo babiesInfo = BabiesInfo.create();
        for (int i = 0; i < BabiesInfo.size(); i++) {
            EventsInfo.create(babiesInfo.getBabyInfoId(i));
        }
    }
}
