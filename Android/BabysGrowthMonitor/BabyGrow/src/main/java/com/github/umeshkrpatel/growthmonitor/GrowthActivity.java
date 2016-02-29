package com.github.umeshkrpatel.growthmonitor;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.umeshkrpatel.growthmonitor.data.IAdapter;
import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataProvider;
import com.github.umeshkrpatel.growthmonitor.data.IEventInfo;
import com.github.umeshkrpatel.growthmonitor.data.IVaccines;
import com.github.umeshkrpatel.growthmonitor.prefs.Preferences;
import com.github.umeshkrpatel.growthmonitor.utils.IShapeUtils;

public class GrowthActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        IInfoFragment, View.OnClickListener {

    private static final String TAG = "GrowthActivity";
    private ImageView ivNvBabyMain, ivNvBabySub;
    private GridLayout glDetails;
    private TextView tvNvBabyName;
    private GrowthPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private boolean mIsTimeline = true;

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
                onBabyInfoInteraction(-1, IDataInfo.ACTION_NEW);
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
        ivNvBabyMain =
                (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgBabyMain);
        ivNvBabySub =
                (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgBabySub);
        ivNvBabySub.setOnClickListener(this);

        tvNvBabyName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tvBabyName);

        mPagerAdapter =
            new GrowthPagerAdapter(getSupportFragmentManager(), this);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);

        if (IBabyInfo.size() == 0) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMainView();
        updateNavigationView();
        IEventInfo.create(IBabyInfo.currentBabyInfo().getId());
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
        }
        if (!mIsTimeline) {
            updateTimeline();
        } else {
            finalize();
            this.finish();
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
            updateBabyInfo();
        } else if (id == R.id.nav_babies_list) {
            mIsTimeline = false;
            mViewPager.setAdapter(mPagerAdapter);
            IAdapter adapter = new BabiesAdapter(IBabyInfo.getBabyInfoList(), this);
            GrowthFragment.getOrCreate(this).setAdapter(adapter);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_timeline) {
            updateTimeline();
        } else if (id == R.id.nav_send) {
            // mViewPager.setAdapter(mPagerAdapter);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onEventInfoInteraction(IEventInfo item) {
        updateBabyInfo(IDataInfo.ACTION_UPDATE, item.getEventType(), item.getEventID());
    }

    @Override
    public void onBabyInfoInteraction(int babyId, int action) {
        if (action == IDataInfo.ACTION_UPDATE) {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra(IDataInfo.ACTION_TYPE, IDataInfo.ACTION_UPDATE);
            intent.putExtra(IDataInfo.ACTION_EVENT, IDataInfo.EVENT_LIFEEVENT);
            intent.putExtra(IDataInfo.ACTION_VALUE, babyId);
            startActivity(intent);
        } else if (action == IDataInfo.ACTION_DELETE) {
            IBabyInfo.delete(babyId);
            if (IBabyInfo.size() == 0) {
                Intent intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
            } else {
                updateMainView();
                updateNavigationView();
            }
        } else {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onUpdateBabyInfo() {
        updateTimeline();
    }

    @Override
    public void onUpdateGrowthInfo() {
        updateTimeline();
    }

    @Override
    public void onUpdateVaccineInfo() {
        updateTimeline();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        IBabyInfo.moveToNext();
        updateMainView();
        updateNavigationView();
        updateTimeline();
    }

    private void updateMainView() {
        IBabyInfo info = IBabyInfo.currentBabyInfo();
        TextView tvBabyName = (TextView) findViewById(R.id.cgBabyName);
        TextView tvBabyDOB = (TextView) findViewById(R.id.cgBabyDob);
        ImageView ivBabyPicture = (ImageView) findViewById(R.id.cgBabyView);
        long dob = info.getBirthDate();

        tvBabyDOB.setText(Utility.fromMilliSecondsToAge(dob));

        float age = Utility.fromMiliSecondsToMonths(System.currentTimeMillis() - dob);
        IBabyInfo.GenType gender = info.getGender();
        tvBabyName.setText(generateSpannableText(info.getName(), gender.toString()));
        glDetails.setBackgroundResource(IBabyInfo.getBackground(gender));
        int imageId = IBabyInfo.getBabyImage(gender, age);
        ivBabyPicture.setImageResource(imageId);
        ivNvBabyMain.setImageResource(imageId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateNavigationView() {
        IBabyInfo info = IBabyInfo.currentBabyInfo();
        long dob = info.getBirthDate();

        float age = Utility.fromMiliSecondsToMonths(System.currentTimeMillis() - dob);
        IBabyInfo.GenType gender = info.getGender();
        glDetails.setBackground(IShapeUtils.gradientDrawable(info.getId()));
        int imageId = IBabyInfo.getBabyImage(gender, age);
        ivNvBabyMain.setImageResource(imageId);
        tvNvBabyName.setText(info.getName());
        if (IBabyInfo.getBabyInfoCount() > 1) {
            ivNvBabySub.setVisibility(View.VISIBLE);
            info = IBabyInfo.nextBabyInfo();
            dob = info.getBirthDate();
            gender = info.getGender();
            age = Utility.fromMiliSecondsToMonths(System.currentTimeMillis() - dob);
            imageId = IBabyInfo.getBabyImage(gender, age);
            ivNvBabySub.setImageResource(imageId);
        } else {
            ivNvBabySub.setVisibility(View.GONE);
        }
    }

    protected void finalize() {
        //Preferences.saveValue(Preferences.kCurrentBabyID, IBabyInfo.getCurrentIndex());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initialize() {
        Preferences.create(getPreferences(MODE_PRIVATE));

        //IBabyInfo.setCurrentIndex(
        //        Preferences.readValue(Preferences.kCurrentBabyID, Preferences.kDefCurrentBabyID));

        ResourceReader.create(this);
        GradientDrawable drawable =
            (GradientDrawable) getResources().getDrawable(R.drawable.sg_bg_round_blue, null);
        IShapeUtils.setGradientDrawables(drawable);

        IDataProvider.create(this);
        IVaccines.create(this);
        if (IBabyInfo.update() > 0) {
            for (IBabyInfo info : IBabyInfo.getBabyInfoList()) {
                IEventInfo.create(info.getId());
            }
        }
    }
    @NonNull
    private static SpannableString generateSpannableText(
            @NonNull String name, @NonNull String gen) {
        int flen = name.length();
        SpannableString s = new SpannableString(name + '(' + gen + ')');
        s.setSpan(new RelativeSizeSpan(1f), 0, flen, 0);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, flen, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), flen, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.MAGENTA), flen, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(.6f), flen, s.length(), 0);
        return s;
    }

    private void updateTimeline() {
        mIsTimeline = true;
        mViewPager.setAdapter(mPagerAdapter);
        IBabyInfo info = IBabyInfo.currentBabyInfo();
        IAdapter adapter = new TimelineAdapter(IEventInfo.get(info.getId()), this);
        GrowthFragment.getOrCreate(this).setAdapter(adapter);
    }

    private void updateBabyInfo() {
        mIsTimeline = false;
        InfoPagerAdapter adapter =
            new InfoPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(adapter);
    }
    private void updateBabyInfo(int type, int event, int value) {
        mIsTimeline = false;
        InfoPagerAdapter adapter =
            new InfoPagerAdapter(getSupportFragmentManager(), this, type, event, value);
        adapter.notifyDataSetChanged();
        mViewPager.setAdapter(adapter);
    }
}
