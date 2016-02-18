package com.github.umeshkrpatel.growthmonitor.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;

import com.github.umeshkrpatel.growthmonitor.R;
import com.github.umeshkrpatel.growthmonitor.ResourceReader;
import com.github.umeshkrpatel.growthmonitor.Utility;

import java.util.HashMap;

/**
 * Created by weumeshweta on 07-Feb-2016.
 */
public abstract class IVaccines {
    // Vaccine list
    static final int BCG  =   0x1;
    static final int OPV  =   0x2;
    static final int IPV  =   0x4;
    static final int HEPB =   0x8;
    static final int HIB  =   0x10;
    static final int PCV  =   0x20;
    static final int DTP  =   0x40;
    static final int RVV  =   0x80;
    static final int MMR  =   0x100;
    static final int TCV  =   0x200;

    private static String[] vaccineList;
    private static final HashMap<Integer, Integer> vaccineMap;
    static {
        int i = 0;
        vaccineMap = new HashMap<>();
        vaccineMap.put(i++,BCG);
        vaccineMap.put(i++,OPV);
        vaccineMap.put(i++,IPV);
        vaccineMap.put(i++,HEPB);
        vaccineMap.put(i++,HIB);
        vaccineMap.put(i++,PCV);
        vaccineMap.put(i++,DTP);
        vaccineMap.put(i++,RVV);
        vaccineMap.put(i++,MMR);
        vaccineMap.put(i,TCV);
    }

    public static int GetSelectedVaccines(boolean[] selection) {
        int mappedVaccine = 0;
        for (int i : vaccineMap.keySet()) {
            if (selection[i]) {
                mappedVaccine = (mappedVaccine | vaccineMap.get(i));
            }
        }
        return mappedVaccine;
    }

    public static SpannableStringBuilder getVaccineNames(int index) {
        SpannableStringBuilder vaccines = new SpannableStringBuilder();
        if ( (index|BCG) != 0) {
            vaccines.append("\n").append(vaccineList[0]);
        }
        if ( (index|OPV) != 0) {
            vaccines.append("\n").append(vaccineList[1]);
        }
        if ( (index|IPV) != 0) {
            vaccines.append("\n").append(vaccineList[2]);
        }
        if ( (index|HEPB) != 0) {
            vaccines.append("\n").append(vaccineList[3]);
        }
        if ( (index|HIB) != 0) {
            vaccines.append("\n").append(vaccineList[4]);
        }
        if ( (index|PCV) != 0) {
            vaccines.append("\n").append(vaccineList[5]);
        }
        if ( (index|DTP) != 0) {
            vaccines.append("\n").append(vaccineList[6]);
        }
        if ( (index|RVV) != 0) {
            vaccines.append("\n").append(vaccineList[7]);
        }
        if ( (index|MMR) != 0) {
            vaccines.append("\n").append(vaccineList[8]);
        }
        if ( (index|TCV) != 0) {
            vaccines.append("\n").append(vaccineList[9]);
        }
        return vaccines;
    }

    private static class VaccineSchedule {
        final long mDays;
        final int mType;
        VaccineSchedule(long days, int name) {
            mDays = days; mType = name;
        }
    }

    @NonNull
    private static final VaccineSchedule[] vaccineTimeLines = new VaccineSchedule[] {
        new VaccineSchedule(1L, BCG|OPV|HEPB),
        new VaccineSchedule(45L, DTP|IPV|HEPB|HIB|RVV|PCV),
        new VaccineSchedule(75L, DTP|IPV|HIB|RVV|PCV),
        new VaccineSchedule(105L, DTP|IPV|HIB|RVV|PCV),
        new VaccineSchedule(182L, HEPB|OPV),
        new VaccineSchedule(273L, OPV|MMR),
        new VaccineSchedule(365L, TCV),
    };

    public static void schedule(IBabyInfo info) {
        Scheduler task = new Scheduler();
        task.execute(info);
        if (info.action == IDataInfo.ACTION_NEW)
            IEventInfo.create(info.getId());
    }

    private static class Scheduler extends AsyncTask<IBabyInfo, Integer, Integer> {

        @NonNull
        @Override
        protected Integer doInBackground(@NonNull IBabyInfo... params) {
            IDataProvider dp = IDataProvider.get();

            for (IBabyInfo info : params) {
                long date = info.getBirthDate();
                for (VaccineSchedule vc : vaccineTimeLines) {
                    switch (info.action) {
                        case IDataInfo.ACTION_NEW:
                            long dueDate = date + vc.mDays * Utility.kMilliSecondsInDays;
                            dp.addVaccinationInfo(vc.mType, "", dueDate, info.getId());
                            IVaccines.get().createAlarm(dueDate);
                            break;
                        case IDataInfo.ACTION_DELETE:
                            dp.deleteVaccinationInfo(info.getId());
                            break;
                    }
                }
            }
            return 0;
        }
    }

    public static SpannableStringBuilder notificationMessage() {
        long now = System.currentTimeMillis();
        IDataProvider dp = IDataProvider.get();
        SpannableStringBuilder message = new SpannableStringBuilder();
        message.append("Missing item in your calender");
        Cursor c = dp.queryTable(IDataInfo.kVaccineTable, new String[] {IDataInfo.VACCINE_TYPE},
            IDataInfo.DATE + " between " + (now - Utility.kMilliSecondsInDays) + " and " + now,
            null, null, null, null);
        if (c != null && c.getCount() > 0) {
            message.clear();
            message.append(ResourceReader.getString(R.string.vaccine_pending_msg));
            while (c.moveToNext()) {
                message.append(getVaccineNames(c.getInt(0)));
            }
            c.close();
        }
        return message;
    }

    private static IVaccines vaccineScheduler = new EmptyVaccines();
    public static void create(Context context) {
        vaccineScheduler = new Vaccines(context);
        vaccineList = context.getResources().getStringArray(R.array.vaccineListType);
    }

    public static IVaccines get() {
        return vaccineScheduler;
    }

    public abstract void createAlarm(long date);

    private static class Vaccines extends IVaccines {
        private final Context mNotificationContext;
        private Vaccines(@NonNull Context context) {
            mNotificationContext = context;
        }

        @Override
        public void createAlarm(long date) {
            if (date < System.currentTimeMillis()) {
                Intent intent = new Intent(mNotificationContext, NotificationService.class);
                mNotificationContext.startService(intent);
                return;
            }

            AlarmManager alarmManager =
                (AlarmManager) mNotificationContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mNotificationContext, AlarmBroadcastReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(mNotificationContext, 0, intent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, date, pi);
        }
    }

    private static class EmptyVaccines extends IVaccines {
        @Override
        public void createAlarm(long date) {

        }
    }
}
