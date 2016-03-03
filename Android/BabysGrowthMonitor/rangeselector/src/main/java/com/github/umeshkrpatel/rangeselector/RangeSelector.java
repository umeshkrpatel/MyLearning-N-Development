package com.github.umeshkrpatel.rangeselector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;

/**
 * Created by umpatel on 2/29/2016.
 */
public class RangeSelector extends AlertDialog.Builder implements AdapterView.OnItemSelectedListener {
    private ArrayList<Object> mArrayList;
    private final Context mContext;
    private final Activity mActivity;
    private final Spinner mMinSpinner;
    private final Spinner mMaxSpinner;
    private int mCount = 0;
    private SpinnerAdapter mAdapter;
    public RangeSelector(Context context, Activity activity) {
        super(context);
        mContext = context;
        mActivity = activity;
        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_range, null);

        mMaxSpinner = (Spinner)view.findViewById(R.id.maxRange);
        mMinSpinner = (Spinner)view.findViewById(R.id.minRange);
        mMaxSpinner.setOnItemSelectedListener(this);
        mMinSpinner.setOnItemSelectedListener(this);
        this.setView(view);
    }

    @Override
    public RangeSelector setTitle(CharSequence title) {
        super.setTitle(title);
        return this;
    }

    public RangeSelector setAdapter(SpinnerAdapter adapter) {
        this.mAdapter = adapter;
        this.mCount = adapter.getCount();
        mMaxSpinner.setAdapter(adapter);
        mMinSpinner.setAdapter(adapter);
        return this;
    }

    public Object[] getSelectedRanges() {
        Object[] obj = new Object[2];
        obj[0] = mMinSpinner.getSelectedItem();
        obj[1] = mMaxSpinner.getSelectedItem();
        return obj;
    }

    public int[] getSelectedRangePositions() {
        int[] obj = new int[2];
        obj[0] = mMinSpinner.getSelectedItemPosition();
        obj[1] = mMaxSpinner.getSelectedItemPosition();
        return obj;
    }

    public int getMinRange() {
        return mMinSpinner.getSelectedItemPosition();
    }

    public int getMaxRange() {
        return mMaxSpinner.getSelectedItemPosition();
    }

    public void setRangeSelection(int[] selection) {
        if (selection.length == 0) {
            mMinSpinner.setSelection(0);
            mMaxSpinner.setSelection(1);
        }
        if (selection[0] >= mCount - 1)
            selection[0] = mCount - 2;
        mMinSpinner.setSelection(selection[0]);
        if (selection.length == 2) {
            mMaxSpinner.setSelection(selection[1] >= mCount ? mCount - 1 : selection[1]);
        } else {
            mMaxSpinner.setSelection(selection[0] + 1);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mCount == 0)
            return;

        if (parent == mMinSpinner) {
            if (position == (mCount - 1)) {
                mMinSpinner.setSelection(position - 1);
                return;
            }
            if (position >= mMaxSpinner.getSelectedItemPosition()) {
                mMaxSpinner.setSelection(position + 1, true);
            }
        } else {
            if (position == 0) {
                mMaxSpinner.setSelection(position + 1);
                return;
            }
            if (position <= mMinSpinner.getSelectedItemPosition()) {
                mMinSpinner.setSelection(position - 1, true);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
