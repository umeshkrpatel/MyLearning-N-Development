package com.github.umeshkrpatel.growthmonitor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
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

    public static long getDate() {
        return sDateCal.getTimeInMillis();
    }
    public static void setDate(long date) {
        sDateCal.setTimeInMillis(date);
    }
    public static long getTime() {
        return sTimeCal.getTimeInMillis();
    }
    public static void setTime(long time) {
        sTimeCal.setTimeInMillis(time);
    }

    public static String getDateTimeInFormat(long milliseconds, String format) {
        if (format == null)
            format = "dd/MMM/yyyy hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        return dateFormat.format(new Date(milliseconds));
    }

    public static String getDateTimeFromMillisecond(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd,yyyy", Locale.US);
        return dateFormat.format(new Date(milliseconds));
    }

    public static int fromMilliSecondsToDays(long milliseconds) {
        return (int)(milliseconds/kMilliSecondsInDays);
    }

    public static float fromMiliSecondsToMonths(long milliseconds) {
        return (milliseconds/(kMilliSecondsInDays*30));
    }

    public static String fromMilliSecondsToAge(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        int year = calendar.get(Calendar.YEAR) - 1970;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String age = "";
        if (year > 0)
            age = year + "Y:";
        if (month > 0)
            age = age + month + "M:";
        if (day > 0)
            age = age + day + "D:";
        if (hour > 0)
            age = age + hour + "h:";
        if (minute > 0)
            age = age + minute + "m:";
        age = age + second + "s";
        return age;
    }
}
