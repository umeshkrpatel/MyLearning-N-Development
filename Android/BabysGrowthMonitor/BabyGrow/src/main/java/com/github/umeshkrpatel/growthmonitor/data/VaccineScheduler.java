package com.github.umeshkrpatel.growthmonitor.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.umeshkrpatel.growthmonitor.BabiesInfo;
import com.github.umeshkrpatel.growthmonitor.Utility;

/**
 * Created by weumeshweta on 07-Feb-2016.
 */
public class VaccineScheduler {
    // Vaccine list
    static int BCG  =   0x1;
    static int OPV  =   0x2;
    static int IPV  =   0x4;
    static int HEPB =   0x8;
    static int HIB  =   0x10;
    static int PCV  =   0x20;
    static int DTP  =   0x40;
    static int RVV  =   0x80;
    static int MMR  =   0x100;
    static int TCV  =   0x200;

    @Nullable
    private static Context mNorificationContext = null;
    private static VaccineScheduler vaccineScheduler;

    public static VaccineScheduler create(Context context) {
        if (vaccineScheduler == null)
            vaccineScheduler = new VaccineScheduler(context);
        return vaccineScheduler;
    }

    public static VaccineScheduler get() {
        return vaccineScheduler;
    }

    private VaccineScheduler(@Nullable Context context) {
        mNorificationContext = context;
    }

    public void createAlarm(Long date) {
        if (mNorificationContext == null) {
            return;
        }

        if (date < System.currentTimeMillis()) {
            Intent intent = new Intent(mNorificationContext, NotificationService.class);
            mNorificationContext.startService(intent);
        }

        AlarmManager alarmManager =
                (AlarmManager) mNorificationContext.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(mNorificationContext, AlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(mNorificationContext, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, date, pi);
    }

    private static class VaccineSchedule {
        final Long mDays;
        final int mType;
        VaccineSchedule(Long days, int name) {
            mDays = days; mType = name;
        }
    }

    @NonNull
    private static VaccineSchedule[] vaccineTimeLines = new VaccineSchedule[] {
            new VaccineSchedule(0L, BCG|OPV|HEPB),
            new VaccineSchedule(45L, DTP|IPV|HEPB|HIB|RVV|PCV),
            new VaccineSchedule(75L, DTP|IPV|HIB|RVV|PCV),
            new VaccineSchedule(105L, DTP|IPV|HIB|RVV|PCV),
            new VaccineSchedule(182L, HEPB|OPV),
            new VaccineSchedule(273L, OPV|MMR),
            new VaccineSchedule(365L, TCV),
    };

    public static void scheduleVaccination(Long id) {
        new UpdateVaccineSchedule().execute(id);
    }

    public static class UpdateVaccineSchedule extends AsyncTask<Long, Integer, Integer> {

        @NonNull
        @Override
        protected Integer doInBackground(@NonNull Long... params) {
            GrowthDataProvider dp = GrowthDataProvider.get();
            if (dp == null)
                return 0;

            for (Long id: params) {
                BabiesInfo.BabyInfo info = BabiesInfo.getBabyInfoMap().get(id.intValue());
                Long date = info.getDob();
                for (VaccineSchedule vc: vaccineTimeLines) {
                    Long dueDate = date + vc.mDays * Utility.kMilliSecondsInDays;
                    dp.addVaccinationInfo(vc.mType, "", dueDate, info.getId());
                    VaccineScheduler.get().createAlarm(dueDate);
                }
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }

    public static String notificationMessage() {
        Long now = System.currentTimeMillis();
        GrowthDataProvider dp = GrowthDataProvider.get();
        String message = "Missing item in your calender";
        if (dp != null) {
            Cursor c = dp.queryTable(IDataInfo.kVaccineTable, new String[] {IDataInfo.VACCINE_TYPE},
                    IDataInfo.DATE + " between " + (now - Utility.kMilliSecondsInDays) + " and " + now,
                    null, null, null, null);
            if (c != null && c.getCount() > 0) {
                message = "Follwoing vaccination are pending ";
                while (c.moveToNext()) {
                    message =  message + c.getInt(0) + ", ";
                }
            }
        }
        return message;
    }
}
