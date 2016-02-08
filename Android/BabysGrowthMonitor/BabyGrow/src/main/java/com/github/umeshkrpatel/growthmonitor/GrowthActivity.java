package com.github.umeshkrpatel.growthmonitor;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.github.umeshkrpatel.growthmonitor.prefs.Preferences;

public class GrowthActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EventTimelineFragment.OnListFragmentInteractionListener {

    private ImageView ivMainBabyPicture, ivNvBabyPic, ivNvBabyPicExtra;
    private TextView tvMainBabyName, tvNvBabyName, mBabyDob;
    private int mBabyId;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_growth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        ivNvBabyPic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgBabyIcon);
        ivNvBabyPicExtra = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgBabyIconExt);
        tvNvBabyName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvBabyName);
        ivNvBabyPicExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BabysInfo.setToNextIndex();
                updateMainView();
                EventTimelineFragment.newInstance().update();
            }
        });

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        int Index = BabysInfo.getCurrentIndex();
        if (BabysInfo.size() == 0 && Index > 0) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_growth) {
            Intent intent = new Intent(this, GrowthChartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_babies) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_babies_list) {

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
    public void onListFragmentInteraction(EventsInfo.EventItem item) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a GrowthChartFragment (defined as a static inner class below).
            return EventTimelineFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
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
            }
            return null;
        }
    }

    private void updateMainView() {
        BabysInfo babysInfo = BabysInfo.get();
        Integer Index = BabysInfo.getCurrentIndex();
        tvMainBabyName = (TextView)findViewById(R.id.cgBabyName);
        mBabyDob = (TextView) findViewById(R.id.cgBabyDob);
        ivMainBabyPicture = (ImageView) findViewById(R.id.cgBabyView);
        mBabyId = babysInfo.getBabyInfoId(Index);
        Long dob = babysInfo.getBabyInfoDob(Index);
        String gen = babysInfo.getBabyInfoGender(Index);
        tvMainBabyName.setText(babysInfo.getBabyInfoName(Index));
        tvNvBabyName.setText(babysInfo.getBabyInfoName(Index));
        mBabyDob.setText(Utility.getDateTimeFromMillisecond(dob));
        int genId = gen.equals("Girl")?1:0;
        float age = Utility.fromMiliSecondsToMonths(System.currentTimeMillis() - dob);
        int imgR = BabysInfo.getBabyImage(genId, age);
        ivMainBabyPicture.setImageResource(imgR);
        ivNvBabyPic.setImageResource(imgR);
        if (babysInfo.getBabyInfoCount() > 1) {
            ivNvBabyPicExtra.setVisibility(View.VISIBLE);
            Index = (Index + 1) % BabysInfo.size();
            dob = babysInfo.getBabyInfoDob(Index);
            gen = babysInfo.getBabyInfoGender(Index);
            genId = gen.equals("Girl")?1:0;
            age = Utility.fromMiliSecondsToMonths(System.currentTimeMillis() - dob);
            imgR = BabysInfo.getBabyImage(genId, age);
            ivNvBabyPicExtra.setImageResource(imgR);
        } else {
            ivNvBabyPicExtra.setVisibility(View.GONE);
        }
    }

    public void finish() {
        Preferences.saveValue(Preferences.kCurrentBabyID, BabysInfo.getCurrentIndex());
    }
}
