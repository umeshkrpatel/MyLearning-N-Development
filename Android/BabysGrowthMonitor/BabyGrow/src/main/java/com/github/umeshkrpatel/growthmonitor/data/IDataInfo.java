package com.github.umeshkrpatel.growthmonitor.data;

/**
 * Created by umpatel on 1/25/2016.
 */
public interface IDataInfo {
    int MAX_BABY = 5;

    String ACTION_TYPE = "action_type";
    String ACTION_EVENT = "action_event";
    String ACTION_VALUE = "action_value";

    int ACTION_NEW = 0;
    int ACTION_UPDATE = 1;
    int ACTION_DELETE = 2;
    int ACTION_GET = 3;

    /*
     * Baby's life event like first day of smiling, first sound etc
     */
    int EVENT_LIFEEVENT = 0;

    /*
     * Baby's measurement like weight, height etc
     */
    int EVENT_MEASUREMENT = 1;

    /*
     * Baby's vaccination
     */
    int EVENT_VACCINATION = 2;

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
    int INDEX_ID = 0;
    String DATE = "_DATE";
    int INDEX_DATE = 1;
    String BABY_ID = "_BABY_ID";
    int INDEX_BABY_ID = 2;

    // Growth
    String WEIGHT = "_WEIGHT";
    int INDEX_WEIGHT = 3;
    String HEIGHT = "_HEIGHT";
    int INDEX_HEIGHT = 4;
    String HEAD = "_HEAD";
    int INDEX_HEAD = 5;

    // BabyInfo
    String BABY_NAME = "_NAME";
    int INDEX_BABY_NAME = 3;
    String BABY_GENDER = "_GENDER";
    int INDEX_BABY_GENDER = 4;
    String BABY_BGABO = "_BG_ABO";
    int INDEX_BABY_BGABO = 5;
    String THEME_START_COLOR = "_START_COLOR";
    int INDEX_START_COLOR = 6;
    String THEME_CENTER_COLOR = "_CENTER_COLOR";
    int INDEX_CENTER_COLOR = 7;
    String THEME_END_COLOR = "_END_COLOR";
    int INDEX_END_COLOR = 8;

    // Life Event
    String LE_TYPE = "_LE_TYPE";
    int INDEX_LE_TYPE = 3;
    String LE_INFO = "_LE_INFO";
    int INDEX_LE_INFO = 4;

    // Event Below two row will help to map with actual event
    String EVENT_TYPE = "_EVENT_TYPE";
    int INDEX_EVENT_TYPE = 3;
    String EVENT_ID = "_EVENT_ID";
    int INDEX_EVENT_ID = 4;

    // Vaccination
    String VACCINE_TYPE = "_VACCINE_TYPE";
    int INDEX_VACCINE_TYPE = 3;
    String VACCINE_NOTE = "_VACCINE_NOTE";
    int INDEX_VACCINE_NOTE = 4;
    String VACCINE_ISALARM = "_VACCINE_ISALARM";
    int INDEX_VACCINE_ISALARM = 5;
}
