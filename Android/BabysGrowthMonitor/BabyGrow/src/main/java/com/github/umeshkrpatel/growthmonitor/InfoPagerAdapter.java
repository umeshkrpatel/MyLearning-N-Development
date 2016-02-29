package com.github.umeshkrpatel.growthmonitor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class InfoPagerAdapter extends FragmentPagerAdapter {

    private final int actionType, actionEvent, actionValue;
    private final IInfoFragment mListener;
    private static int mBinderID = 10;

    public InfoPagerAdapter(FragmentManager fm, IInfoFragment listener, int type, int event, int value) {
        super(fm);
        mListener = listener;
        actionType = type; actionEvent = event; actionValue = value;
    }

    public InfoPagerAdapter(FragmentManager fm, IInfoFragment listener) {
        super(fm);
        mListener = listener;
        actionType = IDataInfo.ACTION_NEW;
        actionEvent = IDataInfo.EVENT_MEASUREMENT;
        actionValue = -1;
    }

    @Override
    public Fragment getItem(int position) {
        if (actionEvent == IDataInfo.EVENT_LIFEEVENT)
            return AddOrUpdateBaby.getOrCreate(mListener, actionType, actionValue);

        int switchValue = position + 1;
        if (actionType == IDataInfo.ACTION_UPDATE) {
            switchValue = actionEvent;
        }
        switch (switchValue) {
            case IDataInfo.EVENT_MEASUREMENT:
                return AddOrUpdateGrowth.newInstance(mListener, actionType, actionValue);
            case IDataInfo.EVENT_VACCINATION:
                return AddOrUpdateVaccine.getOrCreate(mListener, actionType, actionValue);
        }
        return AddOrUpdateGrowth.newInstance(mListener, actionType, actionValue);
    }

    @Override
    public int getCount() {
        // Baby Info and Growth Info
        if (actionType == IDataInfo.ACTION_UPDATE
            || actionEvent == IDataInfo.EVENT_LIFEEVENT)
            return 1;
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (actionEvent == IDataInfo.EVENT_LIFEEVENT)
            return ResourceReader.getString(R.string.babyInfo);
        int switchValue = position;
        if (actionType == IDataInfo.ACTION_UPDATE) {
            switchValue = actionEvent;
        }
        switch (switchValue) {
            case 0: // IDataInfo.EVENT_MEASUREMENT
                return ResourceReader.getString(R.string.growthInfo);
            case 1: // IDataInfo.EVENT_VACCINATION
                return ResourceReader.getString(R.string.vaccineInfo);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // give an ID different from position when position has been changed
        mBinderID += 2 * position + 1;
        return mBinderID;
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }
}
