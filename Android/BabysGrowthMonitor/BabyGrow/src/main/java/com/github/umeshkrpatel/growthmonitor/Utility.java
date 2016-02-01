package com.github.umeshkrpatel.growthmonitor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by weumeshweta on 26-Jan-2016.
 */
public class Utility {
    public static final long kMilliSecondsInDays = (1000*60*60*24);
    public static final String kDateInddMMyyyy = "dd/MM/yyyy";
    public static final String kDateInddMMMyyyy = "dd/MMM/yyyy";
    public static final String kDateInMMMMddyyyy = "MMMM dd, yyyy";
    public static final String kTimeInhhmm = "hh:mm";
    public static final String kTimeInkkmm = "kk:mm";
    private static final Calendar sDateCal = Calendar.getInstance();
    private static final Calendar sTimeCal = Calendar.getInstance();
    public static void PopupDatePicker(final Context context, final TextView tView, final String format) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                sDateCal.set(year, monthOfYear, dayOfMonth);
                if (tView != null) {
                    tView.setText(getDateTimeInFormat(getDate(), format));
                }
            }
        };

        new DatePickerDialog(context, date, sDateCal.get(Calendar.YEAR),
                sDateCal.get(Calendar.MONTH), sDateCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    public static void PopupTimePicker(final Context context, final TextView tView, final String format) {
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                sTimeCal.set(Calendar.HOUR, hourOfDay);
                sTimeCal.set(Calendar.MINUTE, minute);
                if (tView != null) {
                    tView.setText(getDateTimeInFormat(getTime(), format));
                }
            }
        };

        new TimePickerDialog(context, time, sTimeCal.get(Calendar.HOUR),
                sTimeCal.get(Calendar.MINUTE), true).show();
    }

    public static void resetDateTime() {
        sDateCal.setTimeInMillis(System.currentTimeMillis());
        sTimeCal.setTimeInMillis(System.currentTimeMillis());
    }

    public static Long getDate() {
        return sDateCal.getTimeInMillis();
    }
    public static Long getTime() {
        return sTimeCal.getTimeInMillis();
    }

    public static String getDateTimeInFormat(Long milliseconds, String format) {
        if (format == null)
            format = "dd/MMM/yyyy hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        return dateFormat.format(new Date(milliseconds));
    }

    public static String getDateTimeFromMillisecond(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd,yyyy", Locale.US);
        return dateFormat.format(new Date(milliseconds));
    }

    public static int fromMilliSecondsToDays(Long milliseconds) {
        return (int)(milliseconds/kMilliSecondsInDays);
    }

    public static float fromMiliSecondsToMonths(Long milliseconds) {
        return (milliseconds/(kMilliSecondsInDays*30));
    }

    public enum ChartType {
        AGE,
        WEIGHT,
        HEIGHT,
        HEADCIRCUM,
    }
    public static ArrayList<String> mMonth = new ArrayList<String>()
        {{ add("Birth"); add("03Months"); add("06Months"); add("1.0Year");
           add("1.5Years"); add("2.0Years"); add("2.5Years"); add("3.0Years");
           add("3.5Years"); add("4.0Years"); add("4.5Years"); add("5.0Years");
        }};
    public static final int M3 = 0, M12 = 2, M60 = 10;
    public static int monthsToRange(int months) {
        int range = 0;
        if (months > 3) {
            range = months % 6 + 1;
            if (range > 10)
                range = 10;
        }
        return range;
    }

    public static int rangeToMaxIndex(int range) {
        if (range == 0)
            return 13;
        else {
            return range * 26;
        }
    }

    public static int rangeToMinIndex(int range) {
        if (range == 0)
            return 0;
        return rangeToMaxIndex(range - 1);
    }

    public static int rangeFromDobToToday(Long dob) {
        int months = (int)Utility.fromMiliSecondsToMonths(System.currentTimeMillis() - dob);
        return monthsToRange(months);
    }
}
