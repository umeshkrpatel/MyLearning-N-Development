package com.github.umeshkrpatel.growthmonitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;
import com.github.umeshkrpatel.growthmonitor.data.IGrowthInfo;
import com.github.umeshkrpatel.growthmonitor.data.IValidator;

/**
 * Created by umpatel on 1/25/2016.
 */
public class AddOrUpdateGrowth extends Fragment
    implements View.OnClickListener,
    DialogInterface.OnClickListener {

    private static AddOrUpdateGrowth mInstance = null;
    private static IInfoFragment mListener = null;
    private EditText mWeight, mHeight, mHead, mDate;
    private int actionType = IDataInfo.ACTION_NEW;
    private int actionValue = -1;

    public AddOrUpdateGrowth() {
        Utility.resetDateTime();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    @NonNull
    public static AddOrUpdateGrowth newInstance(IInfoFragment listener, int type, int value) {
        mInstance = new AddOrUpdateGrowth();
        mListener = listener;
        mInstance.setActionData(type, value);
        return mInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.growth_info_fragment, container, false);

        mWeight = (EditText) rootView.findViewById(R.id.etBabyWeight);
        mWeight.addTextChangedListener(new IValidator(mWeight, IValidator.MODE_WEIGHT));
        mHeight = (EditText) rootView.findViewById(R.id.etBabyHeight);
        mHeight.addTextChangedListener(new IValidator(mHeight, IValidator.MODE_HEIGHT));
        mHead = (EditText) rootView.findViewById(R.id.etBabyHeadCircum);
        mHead.addTextChangedListener(new IValidator(mHead, IValidator.MODE_HEADSIZE));
        mDate = (EditText) rootView.findViewById(R.id.etInfoDate);
        mDate.addTextChangedListener(new IValidator(mDate, IValidator.MODE_DATE));

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.PopupDatePicker(getContext(), mDate, Utility.kDateInddMMyyyy);
            }
        });

        Button mSubmit = (Button) rootView.findViewById(R.id.btnGrowth);
        mSubmit.setOnClickListener(this);

        updateView();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        Double weight, height, head;
        long date;
        IBabyInfo babyInfo = IBabyInfo.currentBabyInfo();
        weight = Double.parseDouble(mWeight.getText().toString());
        height = Double.parseDouble(mHeight.getText().toString());
        head = Double.parseDouble(mHead.getText().toString());
        date = Utility.getDateTime();
        if (date < babyInfo.getBirthDate()) {
            Toast.makeText(getContext(), "Invalid Date", Toast.LENGTH_LONG).show();
            mDate.setTextColor(Color.RED);
            return;
        }

        int result = IGrowthInfo.validate(babyInfo.getId(), weight, height, head, date);
        if (result != IError.ERROR_NONE) {
            if ((result & IError.UNDER_WEIGHT) != 0 || (result & IError.OVER_WEIGHT) != 0) {
                mWeight.setTextColor(IValidator.COLOR_WARNING);
            }
            if ((result & IError.UNDER_HEIGHT) != 0 || (result & IError.OVER_HEIGHT) != 0) {
                mHeight.setTextColor(IValidator.COLOR_WARNING);
            }
            if ((result & IError.UNDER_HEADSIZE) != 0 || (result & IError.OVER_HEADSIZE) != 0) {
                mHead.setTextColor(IValidator.COLOR_WARNING);
            }
            if ((result & IError.INVALID_DATE) != 0) {
                mDate.setTextColor(IValidator.COLOR_ERROR);
            }

            String msg = "Invalid Information!!";
            new AlertDialog.Builder(getContext())
                .setTitle(android.R.string.dialog_alert_title).setMessage(msg)
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, null).show();
        } else {
            onClick(null, 0);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Double weight, height, head;
        long date;
        IBabyInfo babyInfo = IBabyInfo.currentBabyInfo();
        weight = Double.parseDouble(mWeight.getText().toString());
        height = Double.parseDouble(mHeight.getText().toString());
        head = Double.parseDouble(mHead.getText().toString());
        date = Utility.getDateTime();
        boolean result;
        if (actionType == IDataInfo.ACTION_UPDATE) {
            result = IGrowthInfo.update(actionValue, babyInfo.getId(), weight, height, head, date);
        } else {
            result = IGrowthInfo.create(babyInfo.getId(), weight, height, head, date);
        }
        if (result) {
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            if (mListener != null)
                mListener.onUpdateVaccineInfo();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setActionData(int type, int value) {
        actionType = type;
        actionValue = value;
    }

    private void updateView() {
        IGrowthInfo info = IGrowthInfo.get(actionValue);
        mWeight.setText(String.valueOf(info.getWeight()));
        mHeight.setText(String.valueOf(info.getHeight()));
        mHead.setText(String.valueOf(info.getHeadSize()));
        Utility.setDateTime(info.getDate());
        mDate.setText(Utility.getDateTimeInFormat(Utility.kDateInddMMyyyy));
    }
}

