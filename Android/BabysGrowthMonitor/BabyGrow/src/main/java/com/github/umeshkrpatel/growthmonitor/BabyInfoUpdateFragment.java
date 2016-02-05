package com.github.umeshkrpatel.growthmonitor;

import android.annotation.TargetApi;
import android.os.Build;
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

    private EditText etName, etDobDate, etDobTime;
    private Switch swGender;
    private Spinner spBGAbo, spBGPh;
    private Button btSubmit;
    private String strBGAbo = "-", strBGPh = "-";
    private String mGender = "Boy";

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.baby_info_fragment, container, false);
        etName = (EditText) rootView.findViewById(R.id.baby_name);
        swGender = (Switch) rootView.findViewById(R.id.baby_gender);
        swGender.setThumbResource(R.drawable.boy_face);
        swGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swGender.isChecked()) {
                    mGender = "Girl";
                    swGender.setThumbResource(R.drawable.girl_face);
                } else {
                    mGender = "Boy";
                    swGender.setThumbResource(R.drawable.boy_face);
                }
            }
        });

        etDobDate = (EditText) rootView.findViewById(R.id.baby_dob);
        etDobTime = (EditText) rootView.findViewById(R.id.baby_dobtime);
        etDobDate.setText(Utility.getDateTimeInFormat(Utility.getDate(), Utility.kDateInddMMyyyy));
        etDobTime.setText(Utility.getDateTimeInFormat(Utility.getTime(), Utility.kTimeInkkmm));
        spBGAbo = (Spinner) rootView.findViewById(R.id.baby_bgABO);
        spBGAbo.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_listview,
                R.id.tvSpinnerList, IDataInfo.BloodGroupABO));
        spBGAbo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strBGAbo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strBGAbo = "-";
            }
        });
        spBGPh = (Spinner) rootView.findViewById(R.id.baby_bgPH);
        spBGPh.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_listview,
                R.id.tvSpinnerList, IDataInfo.BloodGroupPH));
        spBGPh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strBGPh = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strBGPh = "-";
            }
        });
        etDobDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.PopupDatePicker(getContext(), etDobDate, Utility.kDateInddMMyyyy);
            }
        });
        etDobTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.PopupTimePicker(getContext(), etDobTime, Utility.kTimeInkkmm);
            }
        });
        btSubmit = (Button) rootView.findViewById(R.id.baby_add);
        btSubmit.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        String name = etName.getText().toString();
        Long date = Utility.getDate();
        Long time = Utility.getTime();
        GrowthDataProvider dp = GrowthDataProvider.get();
        if (dp.addBabyInfo(name, date, time, mGender, strBGAbo, strBGPh) > -1) {
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            BabysInfo.get().updateBabyInfo();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }
}

