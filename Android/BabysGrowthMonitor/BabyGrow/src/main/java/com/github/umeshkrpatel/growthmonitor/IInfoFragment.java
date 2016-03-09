package com.github.umeshkrpatel.growthmonitor;

import com.github.umeshkrpatel.growthmonitor.data.IEventInfo;

/**
 * Created by weumeshweta on 20-Feb-2016.
 */
public interface IInfoFragment {
    int TIMELINE = 1;
    int BABYLIST = 2;
    int BABYINFO = 3;
    int GROWTHINFO = 4;
    int VACCINEINFO = 5;

    void onEventInfoInteraction(IEventInfo item, int action);
    void onBabyInfoInteraction(int babyId, int action);
    void onUpdateBabyInfo();
    void onUpdateGrowthInfo();
    void onUpdateVaccineInfo();
}
