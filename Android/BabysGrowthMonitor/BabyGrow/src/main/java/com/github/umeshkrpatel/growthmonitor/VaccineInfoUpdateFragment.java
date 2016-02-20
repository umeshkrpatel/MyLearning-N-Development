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
import android.widget.Spinner;
import android.widget.Toast;

import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataProvider;
import com.github.umeshkrpatel.growthmonitor.data.ISpinnerAdapter;
import com.github.umeshkrpatel.growthmonitor.data.IVaccines;
import com.github.umeshkrpatel.multispinner.MultiSpinner;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VaccineInfoUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VaccineInfoUpdateFragment extends Fragment
        implements View.OnClickListener,
        MultiSpinner.MultiSpinnerListener {
    private static final String ARG_ACTION_TYPE = "action_type";
    private static final String ARG_INFO_ID = "info_id";

    private Spinner spBabyInfo;
    private EditText etVDetails, etDate;
    private int mSelectedVaccine;

    private static String[] vaccineTypes = null;

    public VaccineInfoUpdateFragment() {
        // Required empty public constructor
    }

    public static void createVaccineType(Context context) {
        if (vaccineTypes == null)
            vaccineTypes = context.getResources().getStringArray(R.array.vaccineListType);
    }

    public static VaccineInfoUpdateFragment newInstance(int sectionNumber, int infoId) {
        VaccineInfoUpdateFragment fragment = new VaccineInfoUpdateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION_TYPE, sectionNumber);
        args.putInt(ARG_INFO_ID, infoId);
        fragment.setArguments(args);
        return fragment;
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
        spBabyInfo = (Spinner) view.findViewById(R.id.spnBabyInfo);

        etVDetails = (EditText) view.findViewById(R.id.etVaccineDetail);
        etDate = (EditText) view.findViewById(R.id.etInfoDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.PopupDatePicker(getContext(), etDate, "dd/MM/yyyy");
            }
        });

        Button btSubmit = (Button) view.findViewById(R.id.btnVaccine);
        btSubmit.setOnClickListener(this);
        ArrayList<IBabyInfo> babyInfos = IBabyInfo.getBabyInfoList();
        spBabyInfo.setAdapter(new ISpinnerAdapter(getContext(), 0, babyInfos, null));

        MultiSpinner vaccineList = (MultiSpinner) view.findViewById(R.id.etVaccineType);
        vaccineList.setAdapter(
            new ArrayAdapter<>(
                getContext(), R.layout.spinner_listview, R.id.tvSpinnerList, vaccineTypes),
            false, this);
        vaccineList.setAllText(ResourceReader.getString(R.string.allSelected));
        vaccineList.setDefaultText(ResourceReader.getString(R.string.noneSelected));
        return view;
    }

    @Override
    public void onClick(View v) {
        String vaccineDetails;
        long date;
        IBabyInfo babyInfo = (IBabyInfo)spBabyInfo.getSelectedItem();
        vaccineDetails = etVDetails.getText().toString();
        date = Utility.getDate();
        if (date < babyInfo.getBirthDate()) {
            Toast.makeText(getContext(), "Invalid Date", Toast.LENGTH_LONG).show();
            etDate.setTextColor(Color.RED);
            return;
        }
        IDataProvider dp = IDataProvider.get();
        if (dp.addVaccinationInfo(mSelectedVaccine, vaccineDetails, date, babyInfo.getId()) > -1) {
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemsSelected(boolean[] selected) {
        mSelectedVaccine = IVaccines.GetSelectedVaccines(selected);
    }
}
