package com.github.umeshkrpatel.growthmonitor.data;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.github.umeshkrpatel.growthmonitor.R;
import com.github.umeshkrpatel.growthmonitor.ResourceReader;
import com.github.umeshkrpatel.growthmonitor.utils.IShapeUtils;

import java.util.ArrayList;

public abstract class IBabyInfo {

    private static final String TAG = "IBabyInfo";

    public static final int[] sGenIDs = new int[] {
        R.string.gen_boy, R.string.gen_girl, R.string.gen_other
    };

    public enum GenType {
        GEN_BOY, GEN_GIRL, GEN_OTHER;
        @Override
        public String toString() {
            return ResourceReader.getString(sGenIDs[ordinal()]);
        }
        public int toInt() {
            return ordinal();
        }
        public static GenType fromInt(int value) {
            switch (value) {
                case 0:
                    return GenType.GEN_BOY;
                case 1:
                    return GenType.GEN_GIRL;
                default:
                    return GenType.GEN_OTHER;
            }
        }
    }

    public enum BloodGroup {
        BG_NONE(0, "-"),
        A_POS(1, "A+"), B_POS(2, "B+"), AB_POS(3, "AB+"), O_POS(4, "O+"),
        A_NEG(5, "A+"), B_NEG(6, "B+"), AB_NEG(7, "AB+"), O_NEG(8, "O+"),
        ;
        private final int mID;
        private final String mValue;
        BloodGroup(int id, String value) {
            mID = id; mValue = value;
        }

        @Override
        public String toString() {
            return mValue;
        }

        public int toInt() {
            return mID;
        }

        public static BloodGroup fromInt(int id) {
            id = (id < 0 || id > 8) ? 0 : id;
            return BloodGroup.values()[id];
        }
    }

    public int action = IDataInfo.ACTION_NEW;
    public abstract int getId();
    public abstract String getName();
    public abstract GenType getGender();
    public abstract long getBirthDate();
    public abstract BloodGroup getBloodGroup();
    public abstract boolean isActive();
    public abstract void setActive(boolean active);
    public abstract void updateInfo(String name, int gender, long birthDate, int bloodGroup);

    private static final IBabyInfo[] babyInfoList =  {
        new EmptyBaby(), new EmptyBaby(), new EmptyBaby(),
        new EmptyBaby(), new EmptyBaby(), new EmptyBaby()
    };

    public static final int dummyId = (-1);
    private static int mCurrentIndex = 0;
    private static int mCurrentSize = 0;

    // Static Methods
    public static IBabyInfo create(int babyId) {
        if (babyInfoList[babyId].getId() == dummyId) {
            babyInfoList[babyId] = new BabyInfo(babyId);
            IEventInfo.create(babyId);
            mCurrentSize++;
        }
        mCurrentIndex = babyId;
        return babyInfoList[babyId];
    }

    public static void delete(int babyId) {
        if (babyInfoList[babyId].getId() == babyId) {
            babyInfoList[babyId].action = IDataInfo.ACTION_DELETE;
            Scheduler scheduler = new Scheduler();
            scheduler.execute(babyInfoList[babyId]);
            IVaccines.schedule(babyInfoList[babyId]);
            IEventInfo.delete(babyId);
            babyInfoList[babyId] = new EmptyBaby();
            mCurrentSize--;
        }
    }

    public static IBabyInfo get(int babyId) {
        if (babyId < 0)
            return new EmptyBaby();
        return babyInfoList[babyId];
    }

    private static int findFreeId() {
        for (int i = 0; i <= IDataInfo.MAX_BABY; i++) {
            if (babyInfoList[i].getId() == dummyId) {
                return i;
            }
        }
        return dummyId;
    }

    public static int create(String name, int gender, long birthDate,
                             int bloodGroup) {
        int id = findFreeId();
        if (id == dummyId)
            return dummyId;

        IBabyInfo info = create(id);
        info.updateInfo(name, gender, birthDate, bloodGroup);
        Scheduler scheduler = new Scheduler();
        if (scheduler.doInBackground(get(id)) > dummyId) {
            IVaccines.schedule(info);
            IEventInfo.create(id);
        } else {
            Log.e(TAG, "Failed to create info");
            delete(id);
        }
        return id;
    }

    public static int update(int babyId, String name, int gender, long birthDate,
                             int bloodGroup) {
        IBabyInfo info = get(babyId);
        info.action = IDataInfo.ACTION_UPDATE;
        info.updateInfo(name, gender, birthDate, bloodGroup);
        Scheduler scheduler = new Scheduler();
        if (scheduler.doInBackground(get(babyId)) > dummyId)
            return babyId;
        return dummyId;
    }

    public static int update() {
        IDataProvider dp = IDataProvider.get();
        Cursor c = dp.queryTable(IDataInfo.kBabyInfoTable);
        if (c == null || c.getCount() <= 0) {
            return 0;
        }
        mCurrentIndex = 0;
        while (c.moveToNext()) {
            Log.d(TAG, IDataInfo.BABY_ID + "=" + c.getInt(IDataInfo.INDEX_BABY_ID)
                + IDataInfo.BABY_NAME + "=" + c.getString(IDataInfo.INDEX_BABY_NAME)
                + IDataInfo.DATE + "=" + c.getLong(IDataInfo.INDEX_DATE)
            );
            IBabyInfo info = IBabyInfo.create(c.getInt(IDataInfo.INDEX_BABY_ID));
            if (info.getId() > dummyId) {
                info.updateInfo(c.getString(IDataInfo.INDEX_BABY_NAME),
                    c.getInt(IDataInfo.INDEX_BABY_GENDER),
                    c.getLong(IDataInfo.INDEX_DATE),
                    c.getInt(IDataInfo.INDEX_BABY_BGABO)
                );
                IShapeUtils.setGradientColor(info.getId(),
                    c.getInt(IDataInfo.INDEX_START_COLOR),
                    c.getInt(IDataInfo.INDEX_CENTER_COLOR),
                    c.getInt(IDataInfo.INDEX_END_COLOR));
            }
        }
        return mCurrentSize;
    }

    // Helpers
    private static final int[][] BabyImageIDs = new int[][] {
        {R.drawable.boy_sleeping, R.drawable.girl_sleeping} ,
        {R.drawable.boy_sitting, R.drawable.girl_sitting} ,
        {R.drawable.boy_crawling, R.drawable.girl_crawling} ,
    };
    private static final int[] BabyBackgroundIDs = new int[] {
        R.drawable.sg_bg_round_blue, R.drawable.sg_bg_round_pink,
    };
    public static int getBackground(GenType gen) {
        return BabyBackgroundIDs[gen.toInt()%2];
    }
    public static int getBabyImage(GenType gen, float ageMonths) {
        int ageId = 0;
        if (ageMonths >= 12) {
            ageId = 2;
        } else if (ageMonths >= 6) {
            ageId = 1;
        }
        return BabyImageIDs[ageId][gen.toInt()%2];
    }

    public static IBabyInfo currentBabyInfo() {
        int counter = 0;
        while (babyInfoList[mCurrentIndex].getId() == dummyId && counter < IDataInfo.MAX_BABY) {
            moveToNext();
            counter++;
        }
        return babyInfoList[mCurrentIndex];
    }

    public static IBabyInfo nextBabyInfo() {
        int index = (mCurrentIndex + 1) % IDataInfo.MAX_BABY;
        while (babyInfoList[index].getId() == dummyId && index != mCurrentIndex) {
            index = (index + 1) % IDataInfo.MAX_BABY;
        }
        return babyInfoList[index];
    }

    public static void moveToNext() {
        mCurrentIndex = (mCurrentIndex + 1) % IDataInfo.MAX_BABY;
    }

    public static int getBabyInfoCount() {
        return mCurrentSize;
    }

    public static int size() {
        return IBabyInfo.getBabyInfoCount();
    }

    public static ArrayList<IBabyInfo> getBabyInfoList() {
        ArrayList<IBabyInfo> infos = new ArrayList<>();
        for (IBabyInfo info: babyInfoList) {
            if (info.getId() != dummyId) {
                infos.add(info);
            }
        }
        return infos;
    }

    // Overwritten Methods
    private static class BabyInfo extends IBabyInfo {
        final int mBabyId;
        String mName;
        long mBirthDate;
        int mGender, mBloodGroup;
        boolean mIsActive = false;
        public BabyInfo(int babyId) {
            mBabyId = babyId;
        }

        @Override
        public String toString() {
            return mName;
        }

        @Override
        public int getId() {
            return mBabyId;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public GenType getGender() {
            return GenType.fromInt(mGender);
        }

        @Override
        public long getBirthDate() {
            return mBirthDate;
        }

        @Override
        public BloodGroup getBloodGroup() {
            return BloodGroup.fromInt(mBloodGroup);
        }

        @Override
        public boolean isActive() {
            return mCurrentIndex == mBabyId || mIsActive;
        }

        @Override
        public void setActive(boolean active) {
            mIsActive = active;
        }

        @Override
        public void updateInfo(String name, int gender, long birthDate,
                               int bloodGroup) {
            mName = name; mGender = gender; mBirthDate = birthDate;
            mBloodGroup = bloodGroup;
        }
    }

    private static class EmptyBaby extends IBabyInfo {

        @Override
        public int getId() {
            return dummyId;
        }

        @Override
        public String getName() {
            return "None";
        }

        @Override
        public GenType getGender() {
            return GenType.GEN_OTHER;
        }

        @Override
        public long getBirthDate() {
            return System.currentTimeMillis();
        }

        @Override
        public BloodGroup getBloodGroup() {
            return BloodGroup.fromInt(0);
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void setActive(boolean active) {

        }

        @Override
        public void updateInfo(String name, int gender, long birthDate, int bloodGroup) {

        }
    }
    private static class Scheduler extends AsyncTask<IBabyInfo, Integer, Integer> {

        @Override
        protected Integer doInBackground(IBabyInfo... babyInfos) {
            IDataProvider dp = IDataProvider.get();
            Long ret = 0L;
            for (IBabyInfo info: babyInfos) {
                if (info.action == IDataInfo.ACTION_NEW)
                    ret = dp.addBabyInfo(
                        info.getId(), info.getName(), info.getBirthDate(),
                        info.getGender().toInt(), info.getBloodGroup());
                else if (info.action == IDataInfo.ACTION_UPDATE) {
                    ret = dp.updateBabyInfo(
                            info.getId(), info.getName(), info.getBirthDate(),
                            info.getGender().toInt(), info.getBloodGroup());
                }
                else if (info.action == IDataInfo.ACTION_DELETE) {
                    dp.deleteBabyInfo(info.getId());
                }
            }
            return ret.intValue();
        }
    }
}
