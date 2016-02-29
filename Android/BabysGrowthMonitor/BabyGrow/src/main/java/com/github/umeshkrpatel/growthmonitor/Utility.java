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
    public static void PopupDatePicker(
        final Context context, final TextView tView, final String format) {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            sDateCal.set(year, monthOfYear, dayOfMonth);
                if (tView != null) {
                    tView.setText(getDateTimeInFormat(getDateTime(), format));
                }
            }
        };

        new DatePickerDialog(context, date, sDateCal.get(Calendar.YEAR),
            sDateCal.get(Calendar.MONTH), sDateCal.get(Calendar.DAY_OF_MONTH)).show();
    }

    public static void PopupTimePicker(
        final Context context, final TextView tView, final String format) {
        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                sDateCal.set(Calendar.HOUR, hourOfDay);
                sDateCal.set(Calendar.MINUTE, minute);
                if (tView != null) {
                    tView.setText(getDateTimeInFormat(getDateTime(), format));
                }
            }
        };

        new TimePickerDialog(
            context, time, sDateCal.get(Calendar.HOUR), sDateCal.get(Calendar.MINUTE), true
        ).show();
    }

    public static void resetDateTime() {
        sDateCal.setTimeInMillis(System.currentTimeMillis());
    }

    public static long getDateTime() {
        return sDateCal.getTimeInMillis();
    }
    public static void setDateTime(long date) {
        sDateCal.setTimeInMillis(date);
    }

    public static String getDateTimeInFormat(String format) {
        if (format == null)
            format = "dd/MMM/yyyy hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        return dateFormat.format(sDateCal.getTime());
    }

    public static String getDateTimeInFormat(long milliseconds, String format) {
        if (format == null)
            format = "dd/MMM/yyyy hh:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        return dateFormat.format(new Date(milliseconds));
    }

    public static String getDateTimeFromMillisecond(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(kDateInMMMMddyyyy, Locale.US);
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
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        calendar.setTimeInMillis(milliseconds);
        int nyear = calendar.get(Calendar.YEAR);
        int nmonth = calendar.get(Calendar.MONTH);
        int nday = calendar.get(Calendar.DAY_OF_MONTH);
        int nhour = calendar.get(Calendar.HOUR_OF_DAY);
        int nminute = calendar.get(Calendar.MINUTE);
        nminute = minute - nminute;
        if (nminute < 0) {
            nminute = nminute + 60;
            hour = hour - 1;
        }
        nhour = hour - nhour;
        if (nhour < 0) {
            nhour = nhour + 24;
            day = day - 1;
        }
        nday = day - nday;
        if (nday < 0) {
            nday = nday + getDaysOfMonth(month, year);
            month = month - 1;
        }
        nmonth = month - nmonth;
        if (nmonth < 0) {
            nmonth = nmonth + 12;
            year = year - 1;
        }
        nyear = year - nyear;
        String age = ResourceReader.getString(R.string.ageInfo);
        return String.format(age, nyear, nmonth, nday, nhour, nminute);
    }

    static int getDaysOfMonth(int month, int year) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 30;
            case 2:
            {
                if (year % 4 != 0)
                    return 28;
                else if (year % 100 == 0 && year % 400 == 0)
                    return 29;
                else
                    return 28;
            }
            case 4:
            case 6:
            case 9:
            case 11:
            default:
                return 30;
        }
    }
}
