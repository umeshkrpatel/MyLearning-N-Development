package com.github.umeshkrpatel.growthmonitor;

import android.content.Context;
import android.database.Cursor;

import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class EventsInfo {
    private final Integer mBabyID;
    private final Context mContext;
    public static HashMap<Integer, EventsInfo> mEventTimeline = new HashMap<>();
    private ArrayList<EventItem> mEventItems = new ArrayList<>();
    private EventsInfo(final Context context, final Integer babyId) {
        mBabyID = babyId;
        mContext = context;
    }

    public static EventsInfo create(final Context context, final Integer babyId) {
        EventsInfo eventsInfo = mEventTimeline.get(babyId);
        if (eventsInfo == null) {
            eventsInfo = new EventsInfo(context, babyId);
            eventsInfo.update();
            mEventTimeline.put(babyId, eventsInfo);
        }
        return eventsInfo;
    }

    public static EventsInfo get(final Integer babyId) {
        return mEventTimeline.get(babyId);
    }

    public void update() {
        mEventItems.clear();
        Cursor c = GrowthDataProvider.get()
                .queryTable(IDataInfo.kEventTable, null, IDataInfo.BABY_ID + "=" + mBabyID,
                        null, null, null, IDataInfo.DATE + " DESC");
        if (c==null || c.getCount()==0) {
            mEventItems.add(new EventItem(0,0L, 0, 0));
            return;
        }
        while (c.moveToNext()) {
            mEventItems.add(
                    new EventItem(
                            c.getInt(IDataInfo.INDEX_ID),
                            c.getLong(IDataInfo.INDEX_DATE),
                            c.getInt(IDataInfo.INDEX_EVENT_TYPE),
                            c.getInt(IDataInfo.INDEX_EVENT_ID)
                    ));
        }
    }

    public ArrayList<EventItem> getList() {
        return mEventItems;
    }

    public static String getEventDetails(Integer eventType, Integer eventID) {
        Cursor c = null;
        switch (eventType) {
            case IDataInfo.EVENT_MEASUREMENT:
                c = GrowthDataProvider.get()
                        .queryTable(IDataInfo.kGrowthInfoTable, null,
                                IDataInfo.ID + "=" + eventID, null, null, null, null);
                if (c!=null && c.getCount()>0) {
                    if (c.moveToNext()) {
                        return ("She is " + c.getFloat(IDataInfo.INDEX_WEIGHT) + "kg heavy, "
                                + c.getFloat(IDataInfo.INDEX_HEIGHT) + "cm tall, and her head has become "
                                + c.getFloat(IDataInfo.INDEX_HEAD) + "cm big");
                    }
                }
                break;
            case IDataInfo.EVENT_LIFEEVENT:
                c = GrowthDataProvider.get()
                        .queryTable(IDataInfo.kLifeEventTable, null,
                                IDataInfo.ID + "=" + eventID, null, null, null, null);
                if (c!=null && c.getCount()>0) {
                    if (c.moveToNext()) {
                        return ("She has her first " + c.getInt(IDataInfo.INDEX_LE_TYPE) + " today");
                    }
                }
                break;
            case IDataInfo.EVENT_VACCINATION:
                c = GrowthDataProvider.get()
                        .queryTable(IDataInfo.kVaccineTable, null,
                                IDataInfo.ID + "=" + eventID, null, null, null, null);
                if (c!=null && c.getCount()>0) {
                    if (c.moveToNext()) {
                        return ("She has her vaccine " + c.getInt(IDataInfo.INDEX_VACCINE_TYPE) + " today");
                    }
                }
                break;
            default:
                break;
        }
        return "Are we missing something";
    }

    /* Event Timeline Items */
    public class EventItem {
        private final Integer mID;
        private final Long mDate;
        private final Integer mEventType;
        private final Integer mEventID;

        public EventItem(Integer id, Long date, Integer eventType, Integer eventID) {
            mID = id; mDate = date; mEventType = eventType; mEventID = eventID;
        }

        Long getDate() {
            return mDate;
        }
        Integer getEventType() {
            return mEventType;
        }
        Integer getEventID() {
            return mEventID;
        }
    }
}
