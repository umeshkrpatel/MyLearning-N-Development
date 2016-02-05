package com.github.umeshkrpatel.growthmonitor.data;

import com.github.umeshkrpatel.growthmonitor.Utility;

import java.util.ArrayList;

/**
 * Created by umpatel on 2/5/2016.
 */
public class ChartData {
    public static final int M3 = 0;
    public static final int M12 = 2;
    public static final int M60 = 10;
    private static Integer sXAxis = 0;
    private static Integer sYAxis = 1;
    private static Integer sRangeMin = M3;
    private static Integer sRangeMax = M12;
    public static ArrayList<String> mMonth = new ArrayList<String>()
        {{ add("Birth"); add("03 Months"); add("06 Months"); add("1.0 Year");
           add("1.5 Years"); add("2.0 Years"); add("2.5 Years"); add("3.0 Years");
           add("3.5 Years"); add("4.0 Years"); add("4.5 Years"); add("5.0 Years");
        }};

    public static int monthsToRange(int months) {
        int range = 0;
        if (months > 3) {
            range = months % 6 + 1;
            if (range > 10)
                range = 10;
        }
        return range;
    }

    public static int rangeToMaxIndex(int range) {
        if (range == 1 || range == 0)
            return 13;
        else {
            return (range - 1) * 26;
        }
    }

    public static int rangeToMinIndex(int range) {
        if (range == 0)
            return 0;
        if (range == 1)
            return 13;
        return rangeToMaxIndex(range);
    }

    public static int rangeFromDobToToday(Long dob) {
        int months = (int) Utility.fromMiliSecondsToMonths(System.currentTimeMillis() - dob);
        return monthsToRange(months);
    }

    public enum ChartType {
        AGE,
        WEIGHT,
        HEIGHT,
        HEADCIRCUM;
    }

    public static ChartType fromInt(int value) {
        ChartType t = ChartType.values()[value];
        if (t == null) {
            t = ChartType.AGE;
        }
        return t;
    }
    public static void setAxis(Integer x, Integer y) {
        sXAxis = x; sYAxis = y;
    }
    public static void setXAxis(Integer x) {
        sXAxis = x;
    }
    public static ChartType getXAxis() {
        return fromInt(sXAxis);
    }
    public static void setYAxis(Integer y) {
        sYAxis = y;
    }
    public static ChartType getYAxis() {
        return fromInt(sYAxis);
    }
    public static Integer minRange() {
        return sRangeMin;
    }
    public static Integer maxRange() {
        return sRangeMax;
    }
    public static void setMinRange(Integer min) {
        sRangeMin = min;
    }
    public static void setMaxRange(Integer max) {
        sRangeMax = max;
    }
}
