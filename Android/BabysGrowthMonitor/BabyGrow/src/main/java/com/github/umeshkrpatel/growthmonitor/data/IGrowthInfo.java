package com.github.umeshkrpatel.growthmonitor.data;

import android.os.AsyncTask;

/**
 * Created by weumeshweta on 17-Feb-2016.
 */
public abstract class IGrowthInfo {
    public static boolean create(int babyId, double w, double h, double hc, long date) {
        IGrowthInfo info = new GrowthInfo(babyId, w, h, hc, date);
        Scheduler scheduler = new Scheduler();
        return scheduler.doInBackground(info);
    }

    public int getBabyId() {
        return -1;
    }

    public double getWeight() {
        return 0;
    }

    public double getHeight() {
        return 0;
    }

    public double getHeadSize() {
        return 0;
    }

    public long getDate() {
        return 0;
    }

    private static class GrowthInfo extends IGrowthInfo {
        final int mBabyId;
        int mId;
        final double mWeight, mHeight, mHeadSize;
        final long mDate;

        GrowthInfo(int babyId, double w, double h, double hc, long date) {
            mBabyId = babyId;
            mWeight = w;
            mHeight = h;
            mHeadSize = hc;
            mDate = date;
        }

        public int getBabyId() {
            return mBabyId;
        }

        public double getWeight() {
            return mWeight;
        }

        public double getHeight() {
            return mHeight;
        }

        public double getHeadSize() {
            return mHeadSize;
        }

        public long getDate() {
            return mDate;
        }
    }

    private static class Scheduler extends AsyncTask<IGrowthInfo, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(IGrowthInfo... params) {
            IDataProvider dp = IDataProvider.get();
            Boolean result = false;
            for (IGrowthInfo info : params) {
                long eventId = dp.addGrowthInfo(info.getWeight(), info.getHeight(), info.getHeadSize(),
                        info.getDate(), info.getBabyId());
                if (eventId > -1) {
                    result |= IEventInfo.set(((int) eventId), info.getBabyId());
                }
            }
            return result;
        }
    }
}
