package com.github.umeshkrpatel.growthmonitor;

import android.content.Context;

/**
 * Created by weumeshweta on 06-Feb-2016.
 */
public class ResourceReader {
    private static ResourceReader instance = null;
    private final Context mContext;
    public static void create(Context context) {
        if (instance == null)
            instance = new ResourceReader(context);
    }

    public static ResourceReader get() {
        return instance;
    }

    private ResourceReader(final Context context) {
        mContext = context;
    }

    public static String getString(int resourceId) {
        return get().mContext.getString(resourceId);
    }
}
