package com.github.umeshkrpatel.growthmonitor;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by weumeshweta on 26-Feb-2016.
 */
public class GrowthPagerAdapter extends FragmentPagerAdapter {

    private final IInfoFragment mListener;
    public GrowthPagerAdapter(FragmentManager fm,
                              IInfoFragment listener) {
        super(fm);
        mListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return GrowthFragment.getOrCreate(mListener);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
