package com.github.umeshkrpatel.growthmonitor;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.umeshkrpatel.growthmonitor.data.GrowthDataProvider;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class EventsInfo {
    private final Integer mBabyID;
    @NonNull
    public static HashMap<Integer, EventsInfo> mEventTimeline = new HashMap<>();
    @NonNull
    private ArrayList<EventItem> mEventItems = new ArrayList<>();
    private EventsInfo(final Integer babyId) {
        mBabyID = babyId;
    }

    public static EventsInfo create(final Integer babyId) {
        EventsInfo eventsInfo = mEventTimeline.get(babyId);
        if (eventsInfo == null) {
            eventsInfo = new EventsInfo(babyId);
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

    @NonNull
    public ArrayList<EventItem> getList() {
        return mEventItems;
    }

    public static String getEventDetails(@NonNull EventItem item) {
        Cursor c;
        switch (item.mEventType) {
            case IDataInfo.EVENT_MEASUREMENT:
                c = GrowthDataProvider.get()
                        .queryTable(IDataInfo.kGrowthInfoTable, null,
                                IDataInfo.ID + "=" + item.mEventID, null, null, null, null);
                if (c != null && c.getCount() > 0) {
                    if (c.moveToNext()) {
                        String msg = ResourceReader.getString(R.string.event_measurment);
                        String pronoun1, pronoun2;
                        if (BabiesInfo.get().getBabyInfoGender(BabiesInfo.getCurrentIndex()).equals("Girl")) {
                            pronoun1 = ResourceReader.getString(R.string.she);
                            pronoun2 = ResourceReader.getString(R.string.her);
                        } else {
                            pronoun1 = ResourceReader.getString(R.string.he);
                            pronoun2 = ResourceReader.getString(R.string.his);
                        }
                        msg = String.format(msg, pronoun1,
                                c.getFloat(IDataInfo.INDEX_WEIGHT),
                                c.getFloat(IDataInfo.INDEX_HEIGHT),
                                pronoun2,
                                c.getFloat(IDataInfo.INDEX_HEAD)
                                );
                        return msg;
                    }
                }
                break;
            case IDataInfo.EVENT_LIFEEVENT:
                c = GrowthDataProvider.get()
                        .queryTable(IDataInfo.kLifeEventTable, null,
                                IDataInfo.ID + "=" + item.mEventID, null, null, null, null);
                if (c != null && c.getCount() > 0) {
                    if (c.moveToNext()) {
                        return ("She has her first " + c.getInt(IDataInfo.INDEX_LE_TYPE) + " today");
                    }
                }
                break;
            case IDataInfo.EVENT_VACCINATION:
                c = GrowthDataProvider.get()
                        .queryTable(IDataInfo.kVaccineTable, null,
                                IDataInfo.ID + "=" + item.mEventID, null, null, null, null);
                if (c != null && c.getCount() > 0) {
                    if (c.moveToNext()) {
                        String msg = ResourceReader.getString(R.string.event_vaccine);
                        BabiesInfo.BabyInfo info =
                                BabiesInfo.getBabyInfoMap().get(BabiesInfo.getCurrentBabyId());
                        msg = String.format(msg, info.getName(), c.getString(IDataInfo.INDEX_VACCINE_TYPE));
                        return msg;
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

        Integer getID() {
            return mID;
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
