package com.github.umeshkrpatel.growthmonitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

/**
 * Created by umpatel on 1/25/2016.
 */
public class BabyInfoUpdateFragment extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_ACTION_TYPE = "action_type";
    private static final String ARG_INFO_ID = "info_id";

    private EditText mName, mDobDate, mDobTime;
    private Switch mGender;
    private Spinner mBGABO, mBGPH;
    private Button mSubmit;
    private String strBGAbo = "-", strBGPh = "-";

    public BabyInfoUpdateFragment() {
        Utility.resetDateTime();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BabyInfoUpdateFragment newInstance(int sectionNumber, int infoId) {
        BabyInfoUpdateFragment fragment = new BabyInfoUpdateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION_TYPE, sectionNumber);
        args.putInt(ARG_INFO_ID, infoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.baby_info_fragment, container, false);
        mName = (EditText) rootView.findViewById(R.id.baby_name);
        mGender = (Switch) rootView.findViewById(R.id.baby_gender);
        mDobDate = (EditText) rootView.findViewById(R.id.baby_dob);
        mDobTime = (EditText) rootView.findViewById(R.id.baby_dobtime);
        mDobDate.setText(Utility.getDateTimeInFormat(Utility.getDate(), Utility.kDateInddMMyyyy));
        mDobTime.setText(Utility.getDateTimeInFormat(Utility.getTime(), Utility.kTimeInkkmm));
        mBGABO = (Spinner) rootView.findViewById(R.id.baby_bgABO);
        mBGABO.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_listview,
                R.id.spinner_textview, IDataInfo.BloodGroupABO));
        mBGABO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strBGAbo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strBGAbo = "-";
            }
        });
        mBGPH = (Spinner) rootView.findViewById(R.id.baby_bgPH);
        mBGPH.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_listview,
                R.id.spinner_textview, IDataInfo.BloodGroupPH));
        mBGPH.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strBGPh = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strBGPh = "-";
            }
        });
        mDobDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.PopupDatePicker(getContext(), mDobDate, Utility.kDateInddMMyyyy);
            }
        });
        mDobTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.PopupTimePicker(getContext(), mDobTime, Utility.kTimeInkkmm);
            }
        });
        mSubmit = (Button) rootView.findViewById(R.id.baby_add);
        mSubmit.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        String name = mName.getText().toString();
        Integer gender = mGender.isChecked()?0:1;
        Long date = Utility.getDate();
        Long time = Utility.getTime();
        GrowthDataProvider dp = GrowthDataProvider.getInstance(getContext());
        if (dp.addBabyInfo(name, date, time, (gender==0?"Girl":"Boy"), strBGAbo, strBGPh) > -1) {
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            if (BabysInfo.getCurrentBabyIndex() == 0) {
                BabysInfo.getInstance().updateBabyInfo();
            }
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }
}

