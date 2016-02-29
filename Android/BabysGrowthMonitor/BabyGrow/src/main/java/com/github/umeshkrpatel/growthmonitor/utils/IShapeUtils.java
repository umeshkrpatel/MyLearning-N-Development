package com.github.umeshkrpatel.growthmonitor.utils;

import android.annotation.TargetApi;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

/**
 * Created by weumeshweta on 24-Feb-2016.
 */
public class IShapeUtils {
    /*
     * Default color set Pink 0xFFFFA6C1, Blue 0xFF96D6DC
     */
    public static final int COLOR_PINK = 0xFFFFA6C1;
    public static final int COLOR_BLUE = 0xFF96D6DC;
    public static final int COLOR_NONE = 0xFFFEFEFE;
    public static final int COLOR_SHADER = 0x0A0A;
    private static GradientDrawable mGradientDrawables;

    private static int [][] mColors = {
        {COLOR_NONE, COLOR_NONE, COLOR_NONE},
        {COLOR_NONE, COLOR_NONE, COLOR_NONE},
        {COLOR_NONE, COLOR_NONE, COLOR_NONE},
        {COLOR_NONE, COLOR_NONE, COLOR_NONE},
        {COLOR_NONE, COLOR_NONE, COLOR_NONE},
        {COLOR_NONE, COLOR_NONE, COLOR_NONE},
    };

    public static void setGradientDrawables(GradientDrawable drawables) {
        mGradientDrawables = drawables;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static GradientDrawable gradientDrawable(int index) {
        index = index < 0 ? 0 : index;
        mGradientDrawables.setColors(mColors[index % 6]);
        return mGradientDrawables;
    }

    public static int getColor(int index) {
        index = index < 0 ? 0 : index;
        return mColors[index % 6][1];
    }

    public static void setGradientColor(int index, int[] colors) {
        index = index < 0 ? 0 : index;
        System.arraycopy(colors, 0, mColors[index], 0, 3);
    }

    public static void setGradientColor(int index, int centerColor) {
        index = index < 0 ? 0 : index;
        mColors[index][0] = centerColor + COLOR_SHADER;
        mColors[index][1] = centerColor;
        mColors[index][2] = centerColor + COLOR_SHADER;
    }

    public static void setGradientColor(int index, int endColor, int centerColor) {
        index = index < 0 ? 0 : index;
        mColors[index][0] = endColor;
        mColors[index][1] = centerColor;
        mColors[index][2] = endColor;
    }

    public static void setGradientColor(int index, int startColor, int centerColor, int endColor) {
        index = index < 0 ? 0 : index;
        mColors[index][0] = startColor;
        mColors[index][1] = centerColor;
        mColors[index][2] = endColor;
    }
}
