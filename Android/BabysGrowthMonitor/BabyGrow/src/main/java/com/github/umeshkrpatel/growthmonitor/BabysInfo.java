package com.github.umeshkrpatel.growthmonitor;

import android.database.Cursor;

import com.github.umeshkrpatel.growthmonitor.data.GrowthDataProvider;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class BabysInfo {

    private static BabysInfo ourInstance = null;
    ArrayList<BabyInfo> mBabyInfo = new ArrayList<>();
    private final BabyInfo mDummyInfo;
    private static Integer mCurrentBabyInfoIndex = -1;

    public static BabysInfo create() {
        if (ourInstance == null) {
            ourInstance = new BabysInfo();
            ourInstance.updateBabyInfo();
        }
        return ourInstance;
    }

    private static final int[][] BabyImageIDs = new int[][] {
            {R.drawable.boy_sleeping, R.drawable.girl_sleeping} ,
            {R.drawable.boy_sitting, R.drawable.girl_sitting} ,
            {R.drawable.boy_crawling, R.drawable.girl_crawling} ,
    };

    public static int getBabyImage(int gen, float ageMonths) {
        int ageId = 0;
        if (ageMonths >= 12) {
            ageId = 2;
        } else if (ageMonths >= 6) {
            ageId = 1;
        }
        return BabyImageIDs[ageId][gen];
    }

    public static BabysInfo get() {
        return ourInstance;
    }

    public static void setCurrentIndex(int index) {
        mCurrentBabyInfoIndex = index;
    }

    public static void setToNextIndex() {
        mCurrentBabyInfoIndex = (mCurrentBabyInfoIndex + 1) % size();
    }

    public static Integer getCurrentIndex() {
        return mCurrentBabyInfoIndex;
    }
    
    public static Integer getCurrentBabyId() {
        return BabysInfo.get().getBabyInfoId(mCurrentBabyInfoIndex);
    }

    private BabysInfo() {
        mDummyInfo = new BabyInfo(0, "<Dummy>", 0L, 0L, "X", "X", "X");
    }

    public Integer getBabyInfoCount() {
        return mBabyInfo.size();
    }

    public static Integer size() {
        return BabysInfo.get().getBabyInfoCount();
    }

    public void updateBabyInfo() {
        mBabyInfo.clear();
        Cursor c = GrowthDataProvider.get()
                .getInfoFromTable(IDataInfo.kBabyInfoTable);
        if (c == null || c.getCount() <= 0) {
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
        if (index == -1)
            return mDummyInfo.mId;
        return mBabyInfo.get(index).mId;
    }
    public String getBabyInfoName(int index) {
        if (index == -1)
            return mDummyInfo.mName;
        return mBabyInfo.get(index).mName;
    }
    public Long getBabyInfoDob(int index) {
        if (index == -1)
            return mDummyInfo.mDob;
        return mBabyInfo.get(index).mDob;
    }
    public String getBabyInfoGender(int index) {
        if (index == -1)
            return mDummyInfo.mGender;
        return mBabyInfo.get(index).mGender;
    }
    private ArrayList<BabyInfo> babyInfoList() {
        return mBabyInfo;
    }
    public static HashMap<Integer,BabyInfo> getBabyInfoMap() {
        HashMap<Integer, BabyInfo> babyInfos = new HashMap<>();
        for (BabyInfo info: getBabyInfoList()) {
            babyInfos.put(info.getId(), info);
        }
        return babyInfos;
    }
    public static ArrayList<BabyInfo> getBabyInfoList() {
        return BabysInfo.get().babyInfoList();
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
        return BabysInfo.get().babyNamesList();
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

        public Integer getId() {
            return mId;
        }

        public Long getDob() {
            return mDob;
        }

        public Long getTime() {
            return mTime;
        }

        public String getGender() {
            return mGender;
        }

        public String getName() {
            return mName;
        }
    }
}
