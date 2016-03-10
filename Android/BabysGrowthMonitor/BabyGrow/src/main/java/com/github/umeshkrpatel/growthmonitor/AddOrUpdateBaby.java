package com.github.umeshkrpatel.growthmonitor;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;
import com.github.umeshkrpatel.growthmonitor.utils.ColorPickerDialog;
import com.github.umeshkrpatel.growthmonitor.utils.IShapeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by umpatel on 1/25/2016.
 */
public class AddOrUpdateBaby extends Fragment
    implements View.OnClickListener, ColorPickerDialog.OnColorChangedListener {

    private static IInfoFragment mListener;
    private EditText etName, etDobDate, etDobTime;
    private Switch swGender;
    private Spinner mBloodGroup;
    private ImageView mColorChooser;
    private AlertDialog mDialogColor;
    private int actionType = IDataInfo.ACTION_NEW;
    private int actionValue = -1;
    private ImageView colorAccent, colorBlue, colorGreen, colorPrimary, colorPrimaryDark, colorRed;
    private int mColorRId;

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
            final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (savedInstanceState == null && bundle != null) {
            actionType = bundle.getInt(IDataInfo.ACTION_TYPE);
            actionValue = -1;
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

        etDobDate.setOnClickListener(this);
        etDobTime.setOnClickListener(this);
        mColorChooser = (ImageView)rootView.findViewById(R.id.baby_colorChooser);
        mColorChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                mDialogColor = builder.create();
                View colorView = inflater.inflate(R.layout.layout_colors, null);
                mDialogColor.setView(colorView);
                colorAccent = (ImageView) colorView.findViewById(R.id.colorAccent);
                colorAccent.setOnClickListener(this);
                colorBlue = (ImageView) colorView.findViewById(R.id.colorBlue);
                colorBlue.setOnClickListener(this);
                colorGreen = (ImageView) colorView.findViewById(R.id.colorGreen);
                colorGreen.setOnClickListener(this);
                colorPrimary = (ImageView) colorView.findViewById(R.id.colorPrimary);
                colorPrimary.setOnClickListener(this);
                colorPrimaryDark = (ImageView) colorView.findViewById(R.id.colorPrimaryDark);
                colorPrimaryDark.setOnClickListener(this);
                colorRed = (ImageView) colorView.findViewById(R.id.colorRed);
                colorRed.setOnClickListener(this);
                mDialogColor.show();
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
        int rId = v.getId();
        switch (rId) {
            case R.id.baby_add:
            {
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
                    IShapeUtils.setGradientColor(id, getColorCode(mColorRId));
                    Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
            return;

            case R.id.baby_dob:
                Utility.PopupDatePicker(getContext(), etDobDate, Utility.kDateInddMMyyyy);
                return;

            case R.id.baby_dobtime:
                Utility.PopupTimePicker(getContext(), etDobTime, Utility.kTimeInkkmm);
                return;

            case R.id.baby_colorChooser:
                break;

            case R.id.colorAccent:
                mColorRId = R.color.colorAccent;
                break;
            case R.id.colorBlue:
                mColorRId = R.color.blue;
                break;
            case R.id.colorGreen:
                mColorRId = R.color.green;
                break;
            case R.id.colorPrimary:
                mColorRId = R.color.colorPrimary;
                break;
            case R.id.colorPrimaryDark:
                mColorRId = R.color.colorPrimaryDark;
                break;
            case R.id.colorRed:
                mColorRId = R.color.red;
                break;

            default:
                break;
        }
        mColorChooser.setImageResource(mColorRId);
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

    @Override
    public void colorChanged(String key, int color) {

    }

    @ColorInt
    private int getColorCode(@ColorRes int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(colorRes, null);
        }
        return getResources().getColor(colorRes);
    }
}

