package com.github.umeshkrpatel.growthmonitor.data;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;

import com.github.umeshkrpatel.growthmonitor.R;
import com.github.umeshkrpatel.growthmonitor.ResourceReader;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class IEventInfo {

    @NonNull
    private static final HashMap<Integer, ArrayList<IEventInfo>> mEventTimeline = new HashMap<>();

    public static ArrayList<IEventInfo> create(final int babyId) {
        if (!mEventTimeline.containsKey(babyId)) {
            Scheduler scheduler = new Scheduler();
            mEventTimeline.put(babyId, scheduler.doInBackground(new EmptyEvent(babyId)));
        }
        return mEventTimeline.get(babyId);
    }

    public static void delete(final int babyId) {
        delete(babyId, -1);
    }

    public static void delete(final int babyId, int eventId) {
        if (mEventTimeline.containsKey(babyId)) {
            Scheduler scheduler = new Scheduler();
            IEventInfo info = new EmptyEvent(babyId, eventId);
            info.action = IDataInfo.ACTION_DELETE;
            scheduler.doInBackground(info);
            if (eventId == -1)
                mEventTimeline.remove(babyId);
            else {
                ArrayList<IEventInfo> babyInfo = mEventTimeline.get(babyId);
                int i = 0; boolean found = false;
                for (i = 0; i < babyInfo.size(); i++) {
                    if (babyInfo.get(i).getEventID() == eventId) {
                        found = true;
                        break;
                    }
                }
                if (found)
                    babyInfo.remove(i);
            }
        }
    }

    public static ArrayList<IEventInfo> get(int babyId) {
        babyId = babyId < 0? IBabyInfo.currentBabyInfo().getId() : babyId;
        return mEventTimeline.get(babyId);
    }

    public static boolean set(int eventId, int babyId) {
        IEventInfo info = new EmptyEvent(eventId, babyId);
        return get(babyId).addAll(new Scheduler().doInBackground(info));
    }

    public static SpannableStringBuilder getEventDetails(@NonNull IEventInfo item) {
        Cursor c = null;
        SpannableStringBuilder message = new SpannableStringBuilder();
        IBabyInfo info = IBabyInfo.currentBabyInfo();
        String pronoun1, pronoun2;
        if (IBabyInfo.currentBabyInfo().getGender() == IBabyInfo.GenType.GEN_GIRL) {
            pronoun1 = ResourceReader.getString(R.string.she);
            pronoun2 = ResourceReader.getString(R.string.her);
        } else {
            pronoun1 = ResourceReader.getString(R.string.he);
            pronoun2 = ResourceReader.getString(R.string.his);
        }

        switch (item.getEventType()) {
            case IDataInfo.EVENT_MEASUREMENT:
                c = IDataProvider.get()
                    .queryTable(IDataInfo.kGrowthInfoTable, null,
                        IDataInfo.ID + "=" + item.getEventID(), null, null, null, null);
                if (c != null && c.getCount() > 0 && c.moveToNext()) {
                    String msg = ResourceReader.getString(R.string.event_measurment);
                    msg = String.format(msg, pronoun1,
                        c.getFloat(IDataInfo.INDEX_WEIGHT),
                        c.getFloat(IDataInfo.INDEX_HEIGHT),
                        pronoun2,
                        c.getFloat(IDataInfo.INDEX_HEAD)
                    );
                    message.append(msg);
                }
                break;

            case IDataInfo.EVENT_LIFEEVENT:
                c = IDataProvider.get()
                    .queryTable(IDataInfo.kLifeEventTable, null,
                        IDataInfo.ID + "=" + item.getEventID(), null, null, null, null);
                if (c != null && c.getCount() > 0 && c.moveToNext()) {
                    if (c.getInt(IDataInfo.INDEX_LE_TYPE) == IDataInfo.LIFEEVENT_BORN) {
                        String msg = ResourceReader.getString(R.string.lifeEventBorn);
                        msg = String.format(msg, info.getName());
                        message.append(msg);
                        break;
                    } else {
                        message.append("Some lifeevent!");
                    }
                }
                break;

            case IDataInfo.EVENT_VACCINATION:
                c = IDataProvider.get()
                    .queryTable(IDataInfo.kVaccineTable, null,
                        IDataInfo.ID + "=" + item.getEventID(), null, null, null, null);
                if (c != null && c.getCount() > 0 && c.moveToNext()) {
                    String msg = ResourceReader.getString(R.string.event_vaccine);
                    msg = String.format(msg, info.getName());
                    message.append(msg);
                    message.append(
                        IVaccines.getVaccineNames(c.getInt(IDataInfo.INDEX_VACCINE_TYPE))
                    );
                }
                break;

            default:
                break;
        }
        if (c != null && c.getCount() > 0)
            c.close();
        return message;
    }

    private int action = IDataInfo.ACTION_UPDATE;
    protected abstract int getBabyId();
    public abstract int getID();
    public abstract long getDate();
    public abstract int getEventType();
    public abstract int getEventID();

    /* Event Timeline Items */
    private static class EventInfo extends IEventInfo {
        private final int mID;
        private final long mDate;
        private final int mEventType;
        private final int mEventID;

        public EventInfo(int id, long date, int eventType, int eventID) {
            mID = id; mDate = date; mEventType = eventType; mEventID = eventID;
        }

        @Override
        protected int getBabyId() {
            return -1;
        }

        public int getID() {
            return mID;
        }
        public long getDate() {
            return mDate;
        }
        public int getEventType() {
            return mEventType;
        }
        public int getEventID() {
            return mEventID;
        }
    }

    private static class EmptyEvent extends IEventInfo {
        final int mEventId, mBabyId;
        private EmptyEvent() {
            mEventId = -1; mBabyId = -1;
        }
        private EmptyEvent(int babyId) {
            mEventId = -1; mBabyId = babyId;
        }
        private EmptyEvent(int eventId, int babyId) {
            mEventId = eventId; mBabyId = babyId;
        }

        @Override
        protected int getBabyId() {
            return mBabyId;
        }

        @Override
        public int getID() {
            return 0;
        }

        @Override
        public long getDate() {
            return System.currentTimeMillis();
        }

        @Override
        public int getEventType() {
            return 0;
        }

        @Override
        public int getEventID() {
            return mEventId;
        }
    }

    private static class Scheduler extends AsyncTask<IEventInfo, Integer, ArrayList<IEventInfo>> {

        @Override
        protected ArrayList<IEventInfo> doInBackground(IEventInfo... params) {
            ArrayList<IEventInfo> info = new ArrayList<>();
            for (IEventInfo eInfo : params) {
                if (eInfo.getBabyId() < 0) {
                    return info;
                }
                String where = IDataInfo.BABY_ID + "=" + eInfo.getBabyId();
                if (eInfo.action == IDataInfo.ACTION_UPDATE) {
                    if (eInfo.getEventID() > -1) {
                        where = where + " AND " + IDataInfo.EVENT_ID + "=" + eInfo.getEventID();
                    }
                    Cursor c = IDataProvider.get()
                        .queryTable(IDataInfo.kEventTable, null, where, null,
                            null, null, IDataInfo.DATE + " DESC");
                    if (c == null || c.getCount() == 0) {
                        info.add(new EmptyEvent());
                        return info;
                    }
                    while (c.moveToNext()) {
                        info.add(
                            new EventInfo(
                                c.getInt(IDataInfo.INDEX_ID),
                                c.getLong(IDataInfo.INDEX_DATE),
                                c.getInt(IDataInfo.INDEX_EVENT_TYPE),
                                c.getInt(IDataInfo.INDEX_EVENT_ID)
                            )
                        );
                    }
                    c.close();
                } else {
                    if (eInfo.getEventID() != -1) {
                        where = where + " AND " + IDataInfo.EVENT_ID + "=" + eInfo.getEventID();
                    }
                    IDataProvider.get().deleteInfo(IDataInfo.kEventTable, where, null);
                }
            }
            return info;
        }
    }
}
