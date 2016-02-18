package com.github.umeshkrpatel.growthmonitor.prefs;

import android.content.SharedPreferences;

/**
 * Created by weumeshweta on 06-Feb-2016.
 */
public class Preferences {

    public static final String kPackageName = "com.github.umeshkrpatel.growthmonitor.";

    public static String kCurrentBabyID = kPackageName + "PREF_CURRENT_BABY";
    public static int kDefCurrentBabyID = -1;

    private static Preferences ourInstance;

    public static void create(SharedPreferences prefs) {
        ourInstance = new Preferences(prefs);
    }

    private static Preferences get() {
        return ourInstance;
    }

    private final SharedPreferences mSharedPref;

    private Preferences(SharedPreferences prefs) {
        mSharedPref = prefs;
    }

    private void set(String key, String value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String get(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void saveValue(String key, String value) {
        get().set(key,value);
    }

    public static String readValue(String key, String defValue) {
        return get().get(key, defValue);
    }

    private void set(String key, int value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private int get(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void saveValue(String key, int value) {
        get().set(key,value);
    }

    public static int readValue(String key, int defValue) {
        return get().get(key, defValue);
    }

    private void set(String key, long value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private long get(String key, long defValue) {
        return mSharedPref.getLong(key, defValue);
    }

    public static void saveValue(String key, long value) {
        get().set(key,value);
    }

    public static long readValue(String key, long defValue) {
        return get().get(key, defValue);
    }

    private void set(String key, float value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    private float get(String key, float defValue) {
        return mSharedPref.getFloat(key, defValue);
    }

    public static void saveValue(String key, float value) {
        get().set(key,value);
    }

    public static float readValue(String key, float defValue) {
        return get().get(key, defValue);
    }

    private void set(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean get(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void saveValue(String key, boolean value) {
        get().set(key,value);
    }

    public static boolean readValue(String key, boolean defValue) {
        return get().get(key, defValue);
    }
}
