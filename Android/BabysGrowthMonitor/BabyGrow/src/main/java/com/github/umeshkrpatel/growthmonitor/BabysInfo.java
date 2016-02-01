package com.github.umeshkrpatel.growthmonitor;

import android.content.Context;
import android.database.Cursor;

import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

import java.util.ArrayList;

/**
 * Created by weumeshweta on 26-Jan-2016.
 */
public class BabysInfo {

    private static BabysInfo ourInstance = null;
    ArrayList<BabyInfo> mBabyInfo = new ArrayList<>();
    private static Integer mCurrentBabyId = 0;
    private final Context mContext;
    public static BabysInfo CreateInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new BabysInfo(context);
            ourInstance.updateBabyInfo();
        }
        return ourInstance;
    }

    public static BabysInfo getInstance() {
        return ourInstance;
    }

    public static void setCurrentBabyIndex(int index) {
        mCurrentBabyId = index;
    }

    public static Integer getCurrentBabyIndex() {
        return mCurrentBabyId;
    }

    private BabysInfo(Context context) {
        mContext = context;
    }

    public Integer getBabyInfoCount() {
        if (mBabyInfo == null)
            return 0;
        return mBabyInfo.size();
    }

    public void updateBabyInfo() {
        mBabyInfo.clear();
        Cursor c = GrowthDataProvider.getInstance(mContext)
                .getInfoFromTable(IDataInfo.kBabyInfoTable);
        if (c == null || c.getCount() <= 0) {
            mBabyInfo.add(new BabyInfo(0, "<None>", 0L, 0L, "-", "-", "-"));
            return;
        }
        while (c.moveToNext()) {
            mBabyInfo.add(
                    new BabyInfo(
                            c.getInt(IDataInfo.INDEX_ID),
                            c.getString(IDataInfo.INDEX_NAME),
                            c.getLong(IDataInfo.INDEX_DOB_DATE),
                            c.getLong(IDataInfo.INDEX_DOB_TIME),
                            c.getString(IDataInfo.INDEX_GENDER),
                            c.getString(IDataInfo.INDEX_BG_ABO),
                            c.getString(IDataInfo.INDEX_BG_PH)));
        }
    }

    public Integer getBabyInfoId(int index) {
        if (mBabyInfo.size() == 0 || index < 0)
            return -1;
        return mBabyInfo.get(index).mId;
    }
    public String getBabyInfoName(int index) {
        return mBabyInfo.get(index).mName;
    }
    public Long getBabyInfoDob(int index) {
        return mBabyInfo.get(index).mDob;
    }
    public String getBabyInfoGender(int index) {
        return mBabyInfo.get(index).mGender;
    }
    private ArrayList<BabyInfo> babyInfoList() {
        return mBabyInfo;
    }
    public static ArrayList<BabyInfo> getBabyInfoList() {
        return BabysInfo.getInstance().babyInfoList();
    }
    public CharSequence[] babyNamesList() {
        CharSequence[] names = new CharSequence[mBabyInfo.size()];
        int i = 0;
        for (BabyInfo info : mBabyInfo) {
            names[i] = info.mName;
            i++;
        }
        return names;
    }
    public static CharSequence[] getBabyNamesList() {
        return BabysInfo.getInstance().babyNamesList();
    }
    public class BabyInfo {
        final Integer mId;
        final String mName;
        final Long mDob;
        final Long mTime;
        final String mGender;
        final String mBGABO, mBGPH;
        public BabyInfo(int id, String name, long dob, long time,
                        String gender, String abo, String ph) {
            mId = id; mName = name;
            mDob = dob; mTime = time;
            mGender = gender;
            mBGABO = abo; mBGPH = ph;
        }

        @Override
        public String toString() {
            return mName;
        }
    }
}
