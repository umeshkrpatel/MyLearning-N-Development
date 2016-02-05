package com.github.umeshkrpatel.growthmonitor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by umpatel on 1/25/2016.
 */
public class GrowthInfoUpdateFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_ACTION_TYPE = "action_type";
    private static final String ARG_INFO_ID = "info_id";

    private Spinner mBabyInfo;
    private EditText mWeight, mHeight, mHead, mDate;
    private Button mSubmit;

    public GrowthInfoUpdateFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GrowthInfoUpdateFragment newInstance(int sectionNumber, int infoId) {
        GrowthInfoUpdateFragment fragment = new GrowthInfoUpdateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION_TYPE, sectionNumber);
        args.putInt(ARG_INFO_ID, infoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.growth_info_fragment, container, false);
        mBabyInfo = (Spinner) rootView.findViewById(R.id.spnBabyInfo);
        mWeight = (EditText) rootView.findViewById(R.id.etBabyWeight);
        mHeight = (EditText) rootView.findViewById(R.id.etBabyHeight);
        mHead = (EditText) rootView.findViewById(R.id.etBabyHeadCircum);
        mDate = (EditText) rootView.findViewById(R.id.etInfoDate);
        mDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    return;
                Utility.PopupDatePicker(getContext(), mDate, "dd/MM/yyyy");
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.PopupDatePicker(getContext(), mDate, "dd/MM/yyyy");
            }
        });

        mSubmit = (Button) rootView.findViewById(R.id.btnGrowth);
        mSubmit.setOnClickListener(this);
        ArrayList<BabysInfo.BabyInfo> babyInfos = BabysInfo.getBabyInfoList();
        ArrayAdapter<BabysInfo.BabyInfo> babyInfoArrayAdapter =
                new ArrayAdapter<>(getContext(), R.layout.spinner_listview,
                        R.id.tvSpinnerList, babyInfos);
        mBabyInfo.setAdapter(babyInfoArrayAdapter);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        Double weight, height, head;
        Long date;
        BabysInfo.BabyInfo babyInfo = (BabysInfo.BabyInfo)mBabyInfo.getSelectedItem();
        weight = Double.parseDouble(mWeight.getText().toString());
        height = Double.parseDouble(mHeight.getText().toString());
        head = Double.parseDouble(mHead.getText().toString());
        date = Utility.getDate();
        if (date < babyInfo.mDob) {
            Toast.makeText(getContext(), "Invalid Date", Toast.LENGTH_LONG).show();
            mDate.setTextColor(Color.RED);
            return;
        }
        GrowthDataProvider dp = GrowthDataProvider.get();
        if (dp.addGrowthInfo(weight, height, head, date, babyInfo.mId) > -1 ) {
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            EventsInfo info = EventsInfo.get(babyInfo.mId);
            if ( info == null) {
                info = EventsInfo.create(getContext(), babyInfo.mId);
            }
            info.update();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }
}

