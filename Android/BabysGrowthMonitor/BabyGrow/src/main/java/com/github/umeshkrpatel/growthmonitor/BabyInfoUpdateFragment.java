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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

/**
 * Created by umpatel on 1/25/2016.
 */
public class BabyInfoUpdateFragment extends Fragment implements View.OnClickListener {

    private EditText etName, etDobDate, etDobTime;
    private Switch swGender;
    private String strBGAbo = "-", strBGPh = "-";
    private int actionType = IDataInfo.ACTION_NEW;
    private int actionValue = -1;

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
        args.putInt(IDataInfo.ACTION_TYPE, sectionNumber);
        args.putInt(IDataInfo.ACTION_EVENT, infoId);
        fragment.setArguments(args);
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                actionType = bundle.getInt(IDataInfo.ACTION_TYPE);
                if (actionType == IDataInfo.ACTION_UPDATE) {
                    actionValue = bundle.getInt(IDataInfo.ACTION_EVENT);
                }
            }
        }
        View rootView = inflater.inflate(R.layout.baby_info_fragment, container, false);
        etName = (EditText) rootView.findViewById(R.id.baby_name);
        swGender = (Switch) rootView.findViewById(R.id.baby_gender);
        swGender.setThumbResource(R.drawable.boy_face);
        swGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swGender.setThumbResource(R.drawable.girl_face);
                } else {
                    swGender.setThumbResource(R.drawable.boy_face);
                }
            }
        });

        etDobDate = (EditText) rootView.findViewById(R.id.baby_dob);
        etDobTime = (EditText) rootView.findViewById(R.id.baby_dobtime);

        Spinner spBGAbo = (Spinner) rootView.findViewById(R.id.baby_bgABO);
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
        Spinner spBGPh = (Spinner) rootView.findViewById(R.id.baby_bgPH);
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
        Button btSubmit = (Button) rootView.findViewById(R.id.baby_add);
        btSubmit.setOnClickListener(this);
        if (actionType == IDataInfo.ACTION_UPDATE) {
            IBabyInfo info = IBabyInfo.get(actionValue);
            etName.setText(info.getName());
            Utility.setDate(info.getBirthDate());
            Utility.setTime(info.getBirthTime());
            swGender.setChecked(info.getGender() == IBabyInfo.GenType.GEN_GIRL);
        }

        etDobDate.setText(Utility.getDateTimeInFormat(Utility.getDate(), Utility.kDateInddMMyyyy));
        etDobTime.setText(Utility.getDateTimeInFormat(Utility.getTime(), Utility.kTimeInkkmm));

        return rootView;
    }

    @Override
    public void onClick(View v) {
        String name = etName.getText().toString();
        long date = Utility.getDate();
        long time = Utility.getTime();
        int id;
        if (actionType == IDataInfo.ACTION_NEW)
            id = IBabyInfo.create(
                name, swGender.isChecked() ? 1 : 0, date, time, strBGAbo, strBGPh);
        else {
            id = IBabyInfo.update(
                actionValue, name, swGender.isChecked() ? 1 : 0, date, time, strBGAbo, strBGPh);
        }
        if (id > IBabyInfo.dummyId) {
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }
}

