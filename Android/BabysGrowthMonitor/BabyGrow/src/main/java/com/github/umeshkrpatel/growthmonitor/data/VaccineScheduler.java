package com.github.umeshkrpatel.growthmonitor.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.github.umeshkrpatel.growthmonitor.BabysInfo;
import com.github.umeshkrpatel.growthmonitor.Utility;

/**
 * Created by weumeshweta on 07-Feb-2016.
 */
public class VaccineScheduler {
    // Vaccine list
    static String BCG = "Bacillus Calmette–Guérin (BCG)";
    static String OPV = "Oral Polio (OPV)";
    static String IPV = "Inactivated Polio (IPV)";
    static String HEPB = "Hepatitis B (Hep-B)";
    static String HIB = "Haemophilus influenzae type B (HIB)";
    static String PCV = "Pneumococcal Conjugate (PCV)";
    static String DTP = "Diphtheria, Tetanus & Pertussis (DTP)";
    static String RVV = "Rotavirus (RVV)";
    static String MMR = "Measles, Mumps, and Rubella (MMR)";
    static String TCV = "Typhoid Conjugate (TCV)";

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

    private VaccineScheduler(Context context) {
        mNorificationContext = context;
    }

    public void createAlarm(Long date) {
        AlarmManager alarmManager =
                (AlarmManager) mNorificationContext.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(mNorificationContext, AlarmBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(mNorificationContext, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, date, pi);
    }

    private static class VaccineSchedule {
        final Long mDays;
        final String mName;
        VaccineSchedule(Long days, String name) {
            mDays = days; mName = name;
        }
    }

    private static VaccineSchedule[] vaccineTimeLines = new VaccineSchedule[] {
            new VaccineSchedule(0L, BCG),
            new VaccineSchedule(0L, OPV),
            new VaccineSchedule(0L, HEPB),
            new VaccineSchedule(45L, DTP),
            new VaccineSchedule(45L, IPV),
            new VaccineSchedule(45L, HEPB),
            new VaccineSchedule(45L, HIB),
            new VaccineSchedule(45L, RVV),
            new VaccineSchedule(45L, PCV),

            new VaccineSchedule(75L, DTP),
            new VaccineSchedule(75L, IPV),
            new VaccineSchedule(75L, HIB),
            new VaccineSchedule(75L, RVV),
            new VaccineSchedule(75L, PCV),

            new VaccineSchedule(105L, DTP),
            new VaccineSchedule(105L, IPV),
            new VaccineSchedule(105L, HIB),
            new VaccineSchedule(105L, RVV),
            new VaccineSchedule(105L, PCV),

            new VaccineSchedule(182L, HEPB),
            new VaccineSchedule(182L, OPV),

            new VaccineSchedule(273L, OPV),
            new VaccineSchedule(273L, MMR),

            new VaccineSchedule(365L, TCV),
    };

    public static void scheduleVaccination(Long id) {
        new UpdateVaccineSchedule().execute(id);
    }

    public static class UpdateVaccineSchedule extends AsyncTask<Long, Integer, Integer> {

        @Override
        protected Integer doInBackground(Long... params) {
            for (Long id: params) {
                BabysInfo.BabyInfo info = BabysInfo.getBabyInfoMap().get(id.intValue());
                GrowthDataProvider dp = GrowthDataProvider.get();
                Long date = info.getDob();
                for (VaccineSchedule vc: vaccineTimeLines) {
                    Long dueDate = date + vc.mDays * Utility.kMilliSecondsInDays;
                    dp.addVaccinationInfo(vc.mName, "", dueDate, info.getId());
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
}
