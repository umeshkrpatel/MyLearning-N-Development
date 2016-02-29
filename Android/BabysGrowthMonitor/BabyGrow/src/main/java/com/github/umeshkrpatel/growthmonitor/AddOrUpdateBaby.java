package com.github.umeshkrpatel.growthmonitor;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by umpatel on 1/25/2016.
 */
public class AddOrUpdateBaby extends Fragment implements View.OnClickListener {

    private static IInfoFragment mListener;
    private EditText etName, etDobDate, etDobTime;
    private Switch swGender;
    private Spinner mBloodGroup;
    private int actionType = IDataInfo.ACTION_NEW;
    private int actionValue = -1;

    public AddOrUpdateBaby() {
        Utility.resetDateTime();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AddOrUpdateBaby getOrCreate(IInfoFragment listener, int type, int value) {
        AddOrUpdateBaby fragment = new AddOrUpdateBaby();
        mListener = listener;
        Bundle args = new Bundle();
        args.putInt(IDataInfo.ACTION_TYPE, type);
        args.putInt(IDataInfo.ACTION_VALUE, value);
        fragment.setArguments(args);
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (savedInstanceState == null && bundle != null) {
            actionType = bundle.getInt(IDataInfo.ACTION_TYPE);
            if (actionType == IDataInfo.ACTION_UPDATE) {
                actionValue = bundle.getInt(IDataInfo.ACTION_VALUE);
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

        mBloodGroup = (Spinner) rootView.findViewById(R.id.baby_bgABO);
        List<IBabyInfo.BloodGroup> bloodGroups = Arrays.asList(IBabyInfo.BloodGroup.values());
        mBloodGroup.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_listview,
            R.id.tvSpinnerList, bloodGroups));

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

        updateView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public void onClick(View v) {
        String name = etName.getText().toString();
        long date = Utility.getDateTime();
        int id;
        if (actionType == IDataInfo.ACTION_NEW)
            id = IBabyInfo.create(
                name, swGender.isChecked() ? 1 : 0, date, mBloodGroup.getSelectedItemPosition());
        else {
            id = IBabyInfo.update(actionValue, name, swGender.isChecked() ? 1 : 0, date,
                mBloodGroup.getSelectedItemPosition());
        }
        if (id > IBabyInfo.dummyId) {
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateView() {

        IBabyInfo info = IBabyInfo.get(actionValue);
        etName.setText(info.getName());
        swGender.setChecked(info.getGender() == IBabyInfo.GenType.GEN_GIRL);
        Utility.setDateTime(info.getBirthDate());

        mBloodGroup.setSelection(info.getBloodGroup().toInt(), true);
        etDobDate.setText(Utility.getDateTimeInFormat(Utility.kDateInddMMyyyy));
        etDobTime.setText(Utility.getDateTimeInFormat(Utility.kTimeInkkmm));
    }
}

