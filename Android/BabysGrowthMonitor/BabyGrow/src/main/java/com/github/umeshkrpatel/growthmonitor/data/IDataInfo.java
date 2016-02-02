package com.github.umeshkrpatel.growthmonitor.data;

/**
 * Created by umpatel on 1/25/2016.
 */
public interface IDataInfo {
    int ACTION_NEW = 0;
    int ACTION_UPDATE = 1;

    /*
     * Baby's measurement like weight, height etc
     */
    int EVENT_MEASUREMENT = 0;

    /*
     * Baby's vaccination
     */
    int EVENT_VACCINATION = 1;

    /*
     * Baby's life event like first day of smiling, first sound etc
     */
    int EVENT_LIFEEVENT = 2;

    /*
     * List of life events
     */
    int LIFEEVENT_BORN = 0;
    int LIFEEVENT_SMILE = 1;
    int LIFEEVENT_SOUND = 2;
    int LIFEEVENT_CROWL = 3;
    int LIFEEVENT_STEP = 4;
    int LIFEEVENT_CALLMUMMY = 5;
    int LIFEEVENT_CALLPAPA = 6;
    int LIFEEVENT_OTHERS = 7;
    String[] BloodGroupABO = new String[] {"-", "A", "B", "AB", "O"};
    String[] BloodGroupPH = new String[] {"-", "V+", "V-"};

    /*
     * Tables
     */
    String kGrowthInfoTable = "GrowthInfo";
    String kBabyInfoTable = "BabyInfo";
    String kLifeEventTable = "LifeEventInfo";
    String kEventTable = "EventInfo";
    String kVaccineTable = "VaccineInfo";
    /*
     * Common rows in table
     */
    String ID = "_id";
    Integer INDEX_ID = 0;
    String DATE = "_DATE";
    Integer INDEX_DATE = 1;
    String BABY_ID = "_BABY_ID";
    Integer INDEX_BABY_ID = 2;

    // Growth
    String WEIGHT = "_WEIGHT";
    Integer INDEX_WEIGHT = 3;
    String HEIGHT = "_HEIGHT";
    Integer INDEX_HEIGHT = 4;
    String HEAD = "_HEAD";
    Integer INDEX_HEAD = 5;

    // BabyInfo
    String NAME = "_NAME";
    Integer INDEX_NAME = 1;
    String DOB_DATE = "_DOB_DATE";
    Integer INDEX_DOB_DATE = 2;
    String DOB_TIME = "_DOB_TIME";
    Integer INDEX_DOB_TIME = 3;
    String GENDER = "_GENDER";
    Integer INDEX_GENDER = 4;
    String BG_ABO = "_BG_ABO";
    Integer INDEX_BG_ABO = 5;
    String BG_PH = "_BG_PH";
    Integer INDEX_BG_PH = 6;

    // Life Event
    String LE_TYPE = "_LE_TYPE";
    Integer INDEX_LE_TYPE = 3;
    String LE_INFO = "_LE_INFO";
    Integer INDEX_LE_INFO = 4;

    // Event Below two row will help to map with actual event
    String EVENT_TYPE = "_EVENT_TYPE";
    Integer INDEX_EVENT_TYPE = 3;
    String EVENT_ID = "_EVENT_ID";
    Integer INDEX_EVENT_ID = 4;

    // Vaccination
    String VACCINE_TYPE = "_VACCINE_TYPE";
    Integer INDEX_VACCINE_TYPE = 3;
    String VACCINE_NOTE = "_VACCINE_NOTE";
    Integer INDEX_VACCINE_NOTE = 4;
}
