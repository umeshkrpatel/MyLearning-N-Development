package com.github.umeshkrpatel.growthmonitor;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataProvider;
import com.github.umeshkrpatel.growthmonitor.data.IVaccines;
import com.github.umeshkrpatel.multispinner.MultiSpinner;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddOrUpdateVaccine#getOrCreate} factory method to
 * create an instance of this fragment.
 */
public class AddOrUpdateVaccine extends Fragment
        implements View.OnClickListener,
        MultiSpinner.MultiSpinnerListener {

    private static IInfoFragment mListener = null;
    private EditText etVDetails, etDate;
    private MultiSpinner mVaccineList;
    private int mSelectedVaccine;
    private int mBabyId = -1;
    private int mVaccineId = -1;

    private static String[] vaccineTypes = null;

    public AddOrUpdateVaccine() {
        Utility.resetDateTime();
    }

    public static void createVaccineType(Context context) {
        if (vaccineTypes == null)
            vaccineTypes = context.getResources().getStringArray(R.array.vaccineListType);
    }

    public static AddOrUpdateVaccine getOrCreate(IInfoFragment listener, int babyId, int value) {
        AddOrUpdateVaccine instance = new AddOrUpdateVaccine();
        mListener = listener;
        instance.setActionData(babyId, value);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createVaccineType(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vaccine_info_update, container, false);

        etVDetails = (EditText) view.findViewById(R.id.etVaccineDetail);
        etDate = (EditText) view.findViewById(R.id.etInfoDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Utility.PopupDatePicker(getContext(), etDate, Utility.kDateInddMMyyyy);
            }
        });

        Button btSubmit = (Button) view.findViewById(R.id.btnVaccine);
        btSubmit.setOnClickListener(this);

        mVaccineList = (MultiSpinner) view.findViewById(R.id.etVaccineType);
        mVaccineList.setAdapter(
            new ArrayAdapter<>(
                getContext(), R.layout.spinner_listview, R.id.tvSpinnerList, vaccineTypes),
            false, this);
        updateView();
        return view;
    }

    @Override
    public void onClick(View v) {
        IBabyInfo babyInfo = IBabyInfo.currentBabyInfo();
        String note = etVDetails.getText().toString();
        long date = Utility.getDateTime();
        if (date < babyInfo.getBirthDate()) {
            Toast.makeText(getContext(), "Invalid Date", Toast.LENGTH_LONG).show();
            etDate.setTextColor(Color.RED);
            return;
        }
        IDataProvider dp = IDataProvider.get();
        if (dp.addOrUpdateVaccination(
            mVaccineId, mSelectedVaccine, note, date, mBabyId, 0) > -1) {
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            if (mListener != null)
                mListener.onUpdateVaccineInfo();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    @Override
    public void onItemsSelected(boolean[] selected) {
        mSelectedVaccine = IVaccines.GetSelectedVaccines(selected);
    }

    private void setActionData(int babyId, int actionValue) {
        this.mBabyId = babyId;
        this.mVaccineId = actionValue;
    }

    private void updateView() {
        mVaccineList.setAllText(ResourceReader.getString(R.string.allSelected));
        mVaccineList.setDefaultText(ResourceReader.getString(R.string.noneSelected));
        IVaccines.IVaccineInfo info = IVaccines.get(mVaccineId);
        mVaccineList.setSelected(IVaccines.getVaccineSelection(info.getValue()));
        Utility.setDateTime(info.getDate());
        etVDetails.setText(info.getNotes());
        etDate.setText(Utility.getDateTimeInFormat(Utility.kDateInddMMyyyy));
    }
}
