package com.github.umeshkrpatel.growthmonitor.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by umpatel on 1/25/2016.
 */
public abstract class IDataProvider extends SQLiteOpenHelper {
    private static final String TAG = "IDataProvider";
    @Nullable
    private static IDataProvider instance = new NullObject(null);
    private static final String kDatabaseName = "GrowthData.db";

    private IDataProvider(Context context) {
        super(context, kDatabaseName, null, 2);
    }

    @NonNull
    public static IDataProvider create(final Context context) {
        instance = new DataProvider(context);
        return instance;
    }

    @NonNull
    public static IDataProvider get() {
        assert instance != null;
        return instance;
    }

    @Override
    public abstract void onCreate(@NonNull SQLiteDatabase db);

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public abstract long addInfo(String table, ContentValues cv);

    public abstract long updateInfo(String table, Integer rowId, ContentValues cv);

    public abstract long addBabyInfo(
            String name, Long dobDate, Long dobTime, String gender, String bgABO, String bgPH);

    public abstract void deleteBabyInfo(Integer id);

    public abstract long updateBabyInfo(Integer id, String name, Long dobDate, Long dobTime,
                                        String gender, String bgABO, String bgPH);

    public abstract long addGrowthInfo(
            double weight, double height, double head, Long date, Integer baby_id);

    public abstract long addVaccinationInfo(
            Integer vaccineType, String vaccineInfo, Long date, Integer baby_id);

    public abstract long updateGrowthInfo(
            Long id, Long weight, Long height, Long head, Long date, Long baby_id);

    public abstract Cursor queryTable(String table);

    public abstract Cursor queryTable(String table, String[] columns, String selection,
                             String[] selectionArgs, String groupBy, String having,
                             String orderBy);

    private static class DataProvider extends IDataProvider {

        private DataProvider(Context context) {
            super(context);
        }

        public void onCreate(@NonNull SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + IDataInfo.kBabyInfoTable + " ( "
                    + IDataInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + IDataInfo.NAME + " TEXT, "
                    + IDataInfo.DOB_DATE + " LONG, "
                    + IDataInfo.DOB_TIME + " LONG, "
                    + IDataInfo.GENDER + " TEXT, "
                    + IDataInfo.BG_ABO + " TEXT, "
                    + IDataInfo.BG_PH + " TEXT);");

            db.execSQL("CREATE TABLE " + IDataInfo.kGrowthInfoTable + " ( "
                    + IDataInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + IDataInfo.DATE + " LONG, "
                    + IDataInfo.BABY_ID + " INTEGER, "
                    + IDataInfo.WEIGHT + " REAL, "
                    + IDataInfo.HEIGHT + " REAL, "
                    + IDataInfo.HEAD + " REAL);");

            db.execSQL("CREATE TABLE " + IDataInfo.kLifeEventTable + " ( "
                    + IDataInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + IDataInfo.DATE + " LONG, "
                    + IDataInfo.BABY_ID + " INTEGER, "
                    + IDataInfo.LE_TYPE + " INTEGER, "
                    + IDataInfo.LE_INFO + " TEXT);");

            db.execSQL("CREATE TABLE " + IDataInfo.kVaccineTable + " ( "
                    + IDataInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + IDataInfo.DATE + " LONG, "
                    + IDataInfo.BABY_ID + " INTEGER, "
                    + IDataInfo.VACCINE_TYPE + " INTEGER, "
                    + IDataInfo.VACCINE_NOTE + " TEXT, "
                    + IDataInfo.VACCINE_DATE + " LONG);");

            db.execSQL("CREATE TABLE " + IDataInfo.kEventTable + " ( "
                    + IDataInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + IDataInfo.DATE + " LONG, "
                    + IDataInfo.BABY_ID + " INTEGER, "
                    + IDataInfo.EVENT_TYPE + " INTEGER, "
                    + IDataInfo.EVENT_ID + " INTEGER);");
        }

        @Override
        public long addInfo(String table, ContentValues cv) {
            Long rowId;
            SQLiteDatabase db = getWritableDatabase();
            rowId = db.insert(table, null, cv);
            return rowId;
        }

        @Override
        public long updateInfo(String table, Integer rowId, ContentValues cv) {
            SQLiteDatabase db = getWritableDatabase();
            rowId = db.update(table, cv, IDataInfo.ID + "=" + rowId, null);
            return rowId;
        }

        @Override
        public long addBabyInfo(
                String name, Long dobDate, Long dobTime, String gender, String bgABO, String bgPH) {
            long rowId;
            ContentValues cv = new ContentValues();
            cv.put(IDataInfo.NAME, name);
            cv.put(IDataInfo.DOB_DATE, dobDate);
            cv.put(IDataInfo.DOB_TIME, dobTime);
            cv.put(IDataInfo.GENDER, gender);
            cv.put(IDataInfo.BG_ABO, bgABO);
            cv.put(IDataInfo.BG_PH, bgPH);
            rowId = addInfo(IDataInfo.kBabyInfoTable, cv);
            if (rowId > -1) {
                long eventId;
                cv.clear();
                cv.put(IDataInfo.LE_TYPE, IDataInfo.LIFEEVENT_BORN);
                cv.put(IDataInfo.LE_INFO, "Born");
                cv.put(IDataInfo.DATE, dobDate);
                cv.put(IDataInfo.BABY_ID, rowId);
                eventId = addInfo(IDataInfo.kLifeEventTable, cv);
                if (eventId > -1) {
                    cv.clear();
                    cv.put(IDataInfo.EVENT_TYPE, IDataInfo.EVENT_LIFEEVENT);
                    cv.put(IDataInfo.EVENT_ID, eventId);
                    cv.put(IDataInfo.BABY_ID, rowId);
                    cv.put(IDataInfo.DATE, dobDate);
                    addInfo(IDataInfo.kEventTable, cv);
                }
            }
            return rowId;
        }

        @Override
        public void deleteBabyInfo(Integer id) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(IDataInfo.kBabyInfoTable, IDataInfo.ID + "=" + id, null);
        }

        public long updateBabyInfo(Integer id, String name, Long dobDate, Long dobTime,
                                   String gender, String bgABO, String bgPH) {
            long ret;
            ContentValues cv = new ContentValues();
            cv.put(IDataInfo.NAME, name);
            cv.put(IDataInfo.DOB_DATE, dobDate);
            cv.put(IDataInfo.DOB_TIME, dobTime);
            cv.put(IDataInfo.GENDER, gender);
            cv.put(IDataInfo.BG_ABO, bgABO);
            cv.put(IDataInfo.BG_PH, bgPH);
            ret = updateInfo(IDataInfo.kBabyInfoTable, id, cv);
            return ret;
        }

        public long addGrowthInfo(
                double weight, double height, double head, Long date, Integer baby_id) {
            long ret;
            Log.d(TAG, "Weight " + weight + " Height " + height + " Head " + head + " Date " + date);
            ContentValues cv = new ContentValues();
            cv.put(IDataInfo.WEIGHT, weight);
            cv.put(IDataInfo.HEIGHT, height);
            cv.put(IDataInfo.HEAD, head);
            cv.put(IDataInfo.DATE, date);
            cv.put(IDataInfo.BABY_ID, baby_id);
            ret = addInfo(IDataInfo.kGrowthInfoTable, cv);
            if (ret > -1) {
                cv.clear();
                cv.put(IDataInfo.EVENT_TYPE, IDataInfo.EVENT_MEASUREMENT);
                cv.put(IDataInfo.EVENT_ID, ret);
                cv.put(IDataInfo.BABY_ID, baby_id);
                cv.put(IDataInfo.DATE, date);
                addInfo(IDataInfo.kEventTable, cv);
            }
            return ret;
        }

        public long addVaccinationInfo(
                Integer vaccineType, String vaccineInfo, Long date, Integer baby_id) {
            ContentValues cv = new ContentValues();
            cv.put(IDataInfo.VACCINE_TYPE, vaccineType);
            cv.put(IDataInfo.VACCINE_NOTE, vaccineInfo);
            cv.put(IDataInfo.DATE, date);
            cv.put(IDataInfo.BABY_ID, baby_id);
            long ret = addInfo(IDataInfo.kVaccineTable, cv);
            if (ret > -1) {
                cv.clear();
                cv.put(IDataInfo.EVENT_TYPE, IDataInfo.EVENT_VACCINATION);
                cv.put(IDataInfo.EVENT_ID, ret);
                cv.put(IDataInfo.BABY_ID, baby_id);
                cv.put(IDataInfo.DATE, date);
                return addInfo(IDataInfo.kEventTable, cv);
            }
            return ret;
        }

        public long updateGrowthInfo(
                Long id, Long weight, Long height, Long head, Long date, Long baby_id) {
            long ret;
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(IDataInfo.WEIGHT, weight);
            cv.put(IDataInfo.HEIGHT, height);
            cv.put(IDataInfo.HEAD, head);
            cv.put(IDataInfo.DATE, date);
            cv.put(IDataInfo.BABY_ID, baby_id);
            ret = db.update(IDataInfo.kGrowthInfoTable, cv, "_ID=" + id, null);
            return ret;
        }

        public Cursor queryTable(String table) {
            return queryTable(table, null, null, null, null, null, null);
        }

        public Cursor queryTable(String table, String[] columns, String selection,
                                 String[] selectionArgs, String groupBy, String having,
                                 String orderBy) {
            SQLiteDatabase db = getReadableDatabase();
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }

    }

    private static class NullObject extends IDataProvider {

        private NullObject(Context context) {
            super(context);
        }

        @Override
        public void onCreate(@NonNull SQLiteDatabase db) {

        }

        @Override
        public long addInfo(String table, ContentValues cv) {
            return 0;
        }

        @Override
        public long updateInfo(String table, Integer rowId, ContentValues cv) {
            return 0;
        }

        @Override
        public long addBabyInfo(
                String name, Long dobDate, Long dobTime, String gender, String bgABO, String bgPH) {
            return 0;
        }

        @Override
        public void deleteBabyInfo(Integer id) {
            return;
        }

        @Override
        public long updateBabyInfo(Integer id, String name, Long dobDate, Long dobTime,
                                   String gender, String bgABO, String bgPH) {
            return 0;
        }

        @Override
        public long addGrowthInfo(
                double weight, double height, double head, Long date, Integer baby_id) {
            return 0;
        }

        @Override
        public long addVaccinationInfo(
                Integer vaccineType, String vaccineInfo, Long date, Integer baby_id) {
            return 0;
        }

        @Override
        public long updateGrowthInfo(
                Long id, Long weight, Long height, Long head, Long date, Long baby_id) {
            return 0;
        }

        @Override
        public Cursor queryTable(String table) {
            return null;
        }

        @Override
        public Cursor queryTable(
                String table, String[] columns, String selection, String[] selectionArgs,
                String groupBy, String having, String orderBy) {
            return null;
        }
    }
}