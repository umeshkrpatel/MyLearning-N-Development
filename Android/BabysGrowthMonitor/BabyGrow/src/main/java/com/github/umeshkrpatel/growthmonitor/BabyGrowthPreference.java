package com.github.umeshkrpatel.growthmonitor;

/**
 * Created by umpatel on 1/27/2016.
 */
public class BabyGrowthPreference {
    private static BabyGrowthPreference ourInstance = new BabyGrowthPreference();

    public static BabyGrowthPreference getInstance() {
        return ourInstance;
    }

    private BabyGrowthPreference() {
    }
}
