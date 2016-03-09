package com.github.umeshkrpatel.growthmonitor;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;
import com.github.umeshkrpatel.growthmonitor.data.IEventInfo;

public class InfoActivity extends AppCompatActivity implements IInfoFragment {

    private int actionType = IDataInfo.ACTION_NEW;
    private int actionEvent = IDataInfo.EVENT_LIFEEVENT;
    private int actionValue = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent(savedInstanceState);

        setContentView(R.layout.activity_add_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InfoPagerAdapter sectionsPagerAdapter =
            new InfoPagerAdapter(
                getSupportFragmentManager(), this, actionType, actionEvent, actionValue
            );

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(actionEvent);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_info, menu);
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

    void handleIntent(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                actionType = bundle.getInt(IDataInfo.ACTION_TYPE);
                actionEvent = bundle.getInt(IDataInfo.ACTION_EVENT);
                actionValue = bundle.getInt(IDataInfo.ACTION_VALUE);
            }
        }
    }

    @Override
    public void onEventInfoInteraction(IEventInfo item, int action) {

    }

    @Override
    public void onBabyInfoInteraction(int babyId, int action) {

    }

    @Override
    public void onUpdateBabyInfo() {

    }

    @Override
    public void onUpdateGrowthInfo() {

    }

    @Override
    public void onUpdateVaccineInfo() {

    }
}
