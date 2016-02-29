package com.github.umeshkrpatel.growthmonitor.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.umeshkrpatel.growthmonitor.utils.IShapeUtils;

/**
 * Created by umpatel on 1/25/2016.
 */
public abstract class IDataProvider extends SQLiteOpenHelper {
    private static final String TAG = "IDataProvider";
    @Nullable
    private static IDataProvider instance = new NullObject();
    private static final String kDatabaseName = "GrowthData.db";

    private IDataProvider(Context context) {
        super(context, kDatabaseName, null, 2);
    }

    public static void create(final Context context) {
        instance = new DataProvider(context);
    }

    @NonNull
    public static synchronized IDataProvider get() {
        assert instance != null;
        return instance;
    }

    @Override
    public abstract void onCreate(@NonNull SQLiteDatabase db);

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public abstract long addInfo(String table, ContentValues cv);

    public abstract long updateInfo(String table, int rowId, ContentValues cv);
    public abstract long updateInfo(
            String table, ContentValues values, String whereClause, String[] whereArgs);

    public abstract void deleteInfo(String table, String where, String[] whereArg);

    public abstract long addBabyInfo(int babyId, String name, long dobDate,
                                     int gender, IBabyInfo.BloodGroup bloodGroup);

    public abstract void deleteBabyInfo(int id);

    public abstract long updateBabyInfo(int babyId, String name, long dobDate,
                                        int gender, IBabyInfo.BloodGroup bloodGroup);
    public abstract long updateThemeInfo(int babyId, int startColor, int centerColor, int endColor);

    public abstract long addGrowthInfo(
            double weight, double height, double head, long date, int baby_id);

    public abstract long addOrUpdateVaccination(
        int id, int vaccineType, String vaccineInfo, long date, int babyId, int isAlarm);

    public abstract void deleteVaccinationInfo(int babyId);

    public abstract long updateGrowthInfo(
            long id, double weight, double height, double head, long date, long baby_id);

    public abstract Cursor queryTable(String table);

    public abstract Cursor queryTable(String table, String selection,
                                      String[] selectionArgs);

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
                + IDataInfo.DATE + " LONG, "
                + IDataInfo.BABY_ID + " INTEGER NOT NULL UNIQUE, "
                + IDataInfo.BABY_NAME + " TEXT, "
                + IDataInfo.BABY_GENDER + " INTEGER, "
                + IDataInfo.BABY_BGABO + " INTEGER, "
                + IDataInfo.THEME_START_COLOR + " INTEGER, "
                + IDataInfo.THEME_CENTER_COLOR + " INTEGER, "
                + IDataInfo.THEME_END_COLOR + " INTEGER);"
            );

            db.execSQL("CREATE TABLE " + IDataInfo.kGrowthInfoTable + " ( "
                + IDataInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IDataInfo.DATE + " LONG, "
                + IDataInfo.BABY_ID + " INTEGER, "
                + IDataInfo.WEIGHT + " REAL, "
                + IDataInfo.HEIGHT + " REAL, "
                + IDataInfo.HEAD + " REAL);"
            );

            db.execSQL("CREATE TABLE " + IDataInfo.kLifeEventTable + " ( "
                + IDataInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + IDataInfo.DATE + " LONG, "
                + IDataInfo.BABY_ID + " INTEGER, "
                + IDataInfo.LE_TYPE + " INTEGER, "
                + IDataInfo.LE_INFO + " TEXT);"
            );

            db.execSQL("CREATE TABLE " + IDataInfo.kVaccineTable + " ( "
                + IDataInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + IDataInfo.DATE + " LONG, "
                + IDataInfo.BABY_ID + " INTEGER, "
                + IDataInfo.VACCINE_TYPE + " INTEGER, "
                + IDataInfo.VACCINE_NOTE + " TEXT, "
                + IDataInfo.VACCINE_ISALARM + " INTEGER DEFAULT 0);"
            );

            db.execSQL("CREATE TABLE " + IDataInfo.kEventTable + " ( "
                    + IDataInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + IDataInfo.DATE + " LONG, "
                    + IDataInfo.BABY_ID + " INTEGER, "
                    + IDataInfo.EVENT_TYPE + " INTEGER, "
                    + IDataInfo.EVENT_ID + " INTEGER);"
            );
        }

        @Override
        public long addInfo(String table, ContentValues cv) {
            long rowId;
            SQLiteDatabase db = getWritableDatabase();
            rowId = db.insert(table, null, cv);
            return rowId;
        }

        @Override
        public long updateInfo(String table, int rowId, ContentValues cv) {
            return getWritableDatabase().update(table, cv, IDataInfo.ID + "=" + rowId, null);
        }

        @Override
        public long updateInfo(
                String table, ContentValues values, String whereClause, String[] whereArgs) {
            return getWritableDatabase().update(table, values, whereClause, whereArgs);
        }

        @Override
        public void deleteInfo(String table, String where, String[] whereArg) {
            getWritableDatabase().delete(table, where, whereArg);
        }

        @Override
        public long addBabyInfo(int babyId, String name, long dobDate, int gender,
                                IBabyInfo.BloodGroup bloodGroup) {
            long rowId;
            int color = gender==0? IShapeUtils.COLOR_BLUE:IShapeUtils.COLOR_PINK;
            ContentValues cv = new ContentValues();
            cv.put(IDataInfo.DATE, dobDate);
            cv.put(IDataInfo.BABY_ID, babyId);
            cv.put(IDataInfo.BABY_NAME, name);
            cv.put(IDataInfo.BABY_GENDER, gender);
            cv.put(IDataInfo.BABY_BGABO, bloodGroup.toInt());
            cv.put(IDataInfo.THEME_START_COLOR, color + IShapeUtils.COLOR_SHADER);
            cv.put(IDataInfo.THEME_CENTER_COLOR, color);
            cv.put(IDataInfo.THEME_END_COLOR, color + IShapeUtils.COLOR_SHADER);
            rowId = addInfo(IDataInfo.kBabyInfoTable, cv);
            if (rowId > -1) {
                long eventId;
                cv.clear();
                cv.put(IDataInfo.LE_TYPE, IDataInfo.LIFEEVENT_BORN);
                cv.put(IDataInfo.LE_INFO, "Born");
                cv.put(IDataInfo.DATE, dobDate);
                cv.put(IDataInfo.BABY_ID, babyId);
                eventId = addInfo(IDataInfo.kLifeEventTable, cv);
                if (eventId > -1) {
                    cv.clear();
                    cv.put(IDataInfo.EVENT_TYPE, IDataInfo.EVENT_LIFEEVENT);
                    cv.put(IDataInfo.EVENT_ID, eventId);
                    cv.put(IDataInfo.BABY_ID, babyId);
                    cv.put(IDataInfo.DATE, dobDate);
                    addInfo(IDataInfo.kEventTable, cv);
                }
            }
            return rowId;
        }

        @Override
        public void deleteBabyInfo(int id) {
            deleteInfo(IDataInfo.kBabyInfoTable, IDataInfo.BABY_ID + "=" + id, null);
        }

        public long updateBabyInfo(int babyId, String name, long dobDate,
                                   int gender, IBabyInfo.BloodGroup bloodGroup) {
            long ret;
            ContentValues cv = new ContentValues();
            cv.put(IDataInfo.DATE, dobDate);
            cv.put(IDataInfo.BABY_NAME, name);
            cv.put(IDataInfo.BABY_GENDER, gender);
            cv.put(IDataInfo.BABY_BGABO, bloodGroup.toInt());
            String where = IDataInfo.BABY_ID + "=" + babyId;
            ret = updateInfo(IDataInfo.kBabyInfoTable, cv, where, null);
            if (ret > -1) {
                long eventId;
                cv.clear();
                where = IDataInfo.LE_TYPE + "=" + IDataInfo.LIFEEVENT_BORN + " AND "
                        + IDataInfo.BABY_ID + "=" + babyId;
                cv.put(IDataInfo.LE_INFO, "Born");
                cv.put(IDataInfo.DATE, dobDate);
                eventId = updateInfo(IDataInfo.kLifeEventTable, cv, where, null);
                if (eventId > 0) {
                    cv.clear();
                    where = IDataInfo.EVENT_TYPE + "=" + IDataInfo.EVENT_LIFEEVENT + " AND "
                            + IDataInfo.BABY_ID + "=" + babyId;
                    cv.put(IDataInfo.DATE, dobDate);
                    updateInfo(IDataInfo.kEventTable, cv, where, null);
                }
            }
            return ret;
        }

        @Override
        public long updateThemeInfo(int babyId, int startColor, int centerColor, int endColor) {
            ContentValues cv = new ContentValues();
            cv.put(IDataInfo.THEME_START_COLOR, startColor);
            cv.put(IDataInfo.THEME_CENTER_COLOR, centerColor);
            cv.put(IDataInfo.THEME_END_COLOR, endColor);
            String where = IDataInfo.BABY_ID + "=" + babyId;
            return updateInfo(IDataInfo.kBabyInfoTable, cv, where, null);
        }

        public long addGrowthInfo(
                double weight, double height, double head, long date, int baby_id) {
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

        public long addOrUpdateVaccination(
            int id, int vaccineType, String vaccineInfo, long date, int babyId, int isAlarm) {
            ContentValues cv = new ContentValues();
            cv.put(IDataInfo.DATE, date);
            cv.put(IDataInfo.BABY_ID, babyId);
            cv.put(IDataInfo.VACCINE_TYPE, vaccineType);
            cv.put(IDataInfo.VACCINE_NOTE, vaccineInfo);
            cv.put(IDataInfo.VACCINE_ISALARM, isAlarm);
            if (id > 0) {
                String where = IDataInfo.ID + "=" + id;
                return updateInfo(IDataInfo.kVaccineTable, cv, where, null);
            }
            long ret = addInfo(IDataInfo.kVaccineTable, cv);
            if (ret > -1 && isAlarm == 0) {
                cv.clear();
                cv.put(IDataInfo.EVENT_TYPE, IDataInfo.EVENT_VACCINATION);
                cv.put(IDataInfo.EVENT_ID, ret);
                cv.put(IDataInfo.BABY_ID, babyId);
                cv.put(IDataInfo.DATE, date);
                addInfo(IDataInfo.kEventTable, cv);
            }
            return ret;
        }

        @Override
        public void deleteVaccinationInfo(int babyId) {
            deleteInfo(IDataInfo.kVaccineTable, IDataInfo.BABY_ID + "=" + babyId, null);
        }

        public long updateGrowthInfo(
                long id, double weight, double height, double head, long date, long baby_id) {
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

        @Override
        public Cursor queryTable(String table, String selection, String[] selectionArgs) {
            return queryTable(table, null, selection, selectionArgs, null, null, null);
        }

        public Cursor queryTable(String table, String[] columns, String selection,
                                 String[] selectionArgs, String groupBy, String having,
                                 String orderBy) {
            SQLiteDatabase db = getReadableDatabase();
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }

    }

    private static class NullObject extends IDataProvider {

        private NullObject() {
            super(null);
        }

        @Override
        public void onCreate(@NonNull SQLiteDatabase db) {

        }

        @Override
        public long addInfo(String table, ContentValues cv) {
            return 0;
        }

        @Override
        public long updateInfo(String table, int rowId, ContentValues cv) {
            return 0;
        }

        @Override
        public long updateInfo(
                String table, ContentValues values, String whereClause, String[] whereArgs) {
            return 0;
        }

        @Override
        public void deleteInfo(String table, String where, String[] whereArg) {

        }

        @Override
        public long addBabyInfo(
            int babyId, String name, long dobDate, int gender, IBabyInfo.BloodGroup bloodGroup) {
            return 0;
        }

        @Override
        public void deleteBabyInfo(int id) {
        }

        @Override
        public long updateBabyInfo(int babyId, String name, long dobDate,
                                   int gender, IBabyInfo.BloodGroup bloodGroup) {
            return 0;
        }

        @Override
        public long updateThemeInfo(int babyId, int startColor, int centerColor, int endColor) {
            return 0;
        }

        @Override
        public long addGrowthInfo(
                double weight, double height, double head, long date, int baby_id) {
            return 0;
        }

        @Override
        public long addOrUpdateVaccination(
            int id, int vaccineType, String vaccineInfo, long date, int babyId, int isAlarm) {
            return 0;
        }

        @Override
        public void deleteVaccinationInfo(int babyId) {
        }

        @Override
        public long updateGrowthInfo(
                long id, double weight, double height, double head, long date, long baby_id) {
            return 0;
        }

        @Override
        public Cursor queryTable(String table) {
            return null;
        }

        @Override
        public Cursor queryTable(String table, String selection, String[] selectionArgs) {
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