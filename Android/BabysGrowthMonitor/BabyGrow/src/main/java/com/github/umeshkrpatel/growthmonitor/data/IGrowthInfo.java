package com.github.umeshkrpatel.growthmonitor.data;

import android.database.Cursor;
import android.os.AsyncTask;

import com.github.umeshkrpatel.growthmonitor.IError;
import com.github.umeshkrpatel.growthmonitor.Utility;

/**
 * Created by weumeshweta on 17-Feb-2016.
 */
public abstract class IGrowthInfo {
    public int action = IDataInfo.ACTION_NEW;
    public static boolean create(int babyId, double w, double h, double hc, long date) {
        IGrowthInfo info = new GrowthInfo(babyId, w, h, hc, date);
        Scheduler scheduler = new Scheduler();
        return scheduler.doInBackground(info) != null;
    }

    public static IGrowthInfo get(int growthId) {
        IGrowthInfo info = new EmptyGrowth(growthId);
        if (growthId < 0)
            return info;
        info.action = IDataInfo.ACTION_GET;
        Scheduler scheduler = new Scheduler();
        return scheduler.doInBackground(info);
    }

    public static boolean update(int id, int babyId, double w, double h, double hc, long date) {
        IGrowthInfo info = new GrowthInfo(id, babyId, w, h, hc, date);
        info.action = IDataInfo.ACTION_UPDATE;
        Scheduler scheduler = new Scheduler();
        return scheduler.doInBackground(info) != null;
    }

    public static int validate(int babyId, double w, double h, double hc, long date) {
        int error = IError.ERROR_NONE;
        IBabyInfo info = IBabyInfo.get(babyId);
        if (info.getBirthDate() > date)
            error = error|IError.INVALID_DATE;
        int week = Utility.fromMilliSecondsToDays(date - info.getBirthDate()) / 7;
        int gender = info.getGender().toInt();
        if (IWPercentileData.Data[gender][week][0] > w)
            error = error|IError.UNDER_WEIGHT;
        else if (w > IWPercentileData.Data[gender][week][13])
            error = error|IError.OVER_WEIGHT;
        if (IHPercentileData.Data[gender][week][0] > h)
            error = error|IError.UNDER_HEIGHT;
        else if (h > IHPercentileData.Data[gender][week][13])
            error = error|IError.OVER_HEIGHT;
        if (IHCPercentileData.Data[gender][week][0] > hc)
            error = error|IError.UNDER_HEADSIZE;
        else if (hc > IHCPercentileData.Data[gender][week][13])
            error = error|IError.OVER_HEADSIZE;
        return error;
    }

    public static boolean validateWeight(double weight) {
        IBabyInfo info = IBabyInfo.currentBabyInfo();
        int week =
            Utility.fromMilliSecondsToDays(System.currentTimeMillis() - info.getBirthDate()) / 7;
        int gender = info.getGender().toInt();
        return !(IWPercentileData.Data[gender][week][0] > weight
            || weight > IWPercentileData.Data[gender][week][13]
        );
    }

    public static boolean validateHeight(double height) {
        IBabyInfo info = IBabyInfo.currentBabyInfo();
        int week =
            Utility.fromMilliSecondsToDays(System.currentTimeMillis() - info.getBirthDate()) / 7;
        int gender = info.getGender().toInt();
        return !(IHPercentileData.Data[gender][week][0] > height
            || height > IHPercentileData.Data[gender][week][13]
        );
    }

    public static boolean validateHeadSize(double headSize) {
        IBabyInfo info = IBabyInfo.currentBabyInfo();
        int week =
            Utility.fromMilliSecondsToDays(System.currentTimeMillis() - info.getBirthDate()) / 7;
        int gender = info.getGender().toInt();
        return !(IHCPercentileData.Data[gender][week][0] > headSize
            || headSize > IHCPercentileData.Data[gender][week][13]
        );
    }

    public abstract int getId();

    public abstract int getBabyId();

    public abstract double getWeight();

    public abstract double getHeight();

    public abstract double getHeadSize();

    public long getDate() {
        return System.currentTimeMillis();
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

        GrowthInfo(int id, int babyId, double w, double h, double hc, long date) {
            mId = id;
            mBabyId = babyId;
            mWeight = w;
            mHeight = h;
            mHeadSize = hc;
            mDate = date;
        }

        @Override
        public int getId() {
            return mId;
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

    private static class EmptyGrowth extends IGrowthInfo {

        final int mGrowthId;
        EmptyGrowth(int growthId) {
            mGrowthId = growthId;
        }

        @Override
        public int getId() {
            return mGrowthId;
        }

        @Override
        public int getBabyId() {
            return 0;
        }

        @Override
        public double getWeight() {
            return 0;
        }

        @Override
        public double getHeight() {
            return 0;
        }

        @Override
        public double getHeadSize() {
            return 0;
        }
    }

    private static class Scheduler extends AsyncTask<IGrowthInfo, Integer, IGrowthInfo> {

        @Override
        protected IGrowthInfo doInBackground(IGrowthInfo... params) {
            IDataProvider dp = IDataProvider.get();
            IGrowthInfo result = null;
            for (IGrowthInfo info : params) {
                if (info.action == IDataInfo.ACTION_NEW) {
                    long eventId = dp.addGrowthInfo(info.getWeight(), info.getHeight(),
                            info.getHeadSize(), info.getDate(), info.getBabyId());
                    if (eventId > -1) {
                        IEventInfo.set(((int) eventId), info.getBabyId());
                        result = info;
                    }
                } else if (info.action == IDataInfo.ACTION_UPDATE){
                    if (dp.updateGrowthInfo(info.getId(), info.getWeight(), info.getHeight(),
                        info.getHeadSize(), info.getDate(), info.getBabyId()) > 0) {
                        result = info;
                    }
                } else if (info.action == IDataInfo.ACTION_GET) {
                    String where = IDataInfo.ID + "=" + info.getId();
                    Cursor cursor = dp.queryTable(IDataInfo.kGrowthInfoTable, where, null);
                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToNext()) {
                        result = new GrowthInfo(
                            cursor.getInt(IDataInfo.INDEX_BABY_ID),
                            cursor.getDouble(IDataInfo.INDEX_WEIGHT),
                            cursor.getDouble(IDataInfo.INDEX_HEIGHT),
                            cursor.getDouble(IDataInfo.INDEX_HEAD),
                            cursor.getLong(IDataInfo.INDEX_DATE)
                        );
                    }
                }
            }
            return result;
        }
    }
}
