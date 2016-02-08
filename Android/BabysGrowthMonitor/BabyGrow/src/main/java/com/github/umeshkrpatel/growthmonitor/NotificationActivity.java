package com.github.umeshkrpatel.growthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.umeshkrpatel.growthmonitor.data.GrowthDataProvider;
import com.github.umeshkrpatel.growthmonitor.data.VaccineScheduler;
import com.github.umeshkrpatel.growthmonitor.prefs.Preferences;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initialize();
        Intent intent = new Intent(this, GrowthActivity.class);
        startActivity(intent);
    }


    private void initialize() {
        Preferences.create(getPreferences(MODE_PRIVATE));

        BabysInfo.setCurrentIndex(
                Preferences.readValue(Preferences.kCurrentBabyID, Preferences.kDefCurrentBabyID));

        ResourceReader.create(this);

        GrowthDataProvider.create(this);
        VaccineScheduler.create(this);
        BabysInfo babysInfo = BabysInfo.create();
        for (int i = 0; i < BabysInfo.size(); i++) {
            EventsInfo.create(babysInfo.getBabyInfoId(i));
        }
    }
}
