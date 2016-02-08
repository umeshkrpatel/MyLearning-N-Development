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

import com.github.umeshkrpatel.growthmonitor.data.GrowthDataProvider;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VaccineInfoUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VaccineInfoUpdateFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_ACTION_TYPE = "action_type";
    private static final String ARG_INFO_ID = "info_id";

    private Spinner spBabyInfo;
    private EditText etVType, etVDetails, etDate;
    private Button btSubmit;

    public VaccineInfoUpdateFragment() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vaccine_info_update, container, false);
        spBabyInfo = (Spinner) view.findViewById(R.id.spnBabyInfo);
        etVType = (EditText) view.findViewById(R.id.etVaccineType);
        etVDetails = (EditText) view.findViewById(R.id.etVaccineDetail);
        etDate = (EditText) view.findViewById(R.id.etInfoDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.PopupDatePicker(getContext(), etDate, "dd/MM/yyyy");
            }
        });
        btSubmit = (Button) view.findViewById(R.id.btnVaccine);
        btSubmit.setOnClickListener(this);
        ArrayList<BabysInfo.BabyInfo> babyInfos = BabysInfo.getBabyInfoList();
        ArrayAdapter<BabysInfo.BabyInfo> babyInfoArrayAdapter =
                new ArrayAdapter<>(getContext(), R.layout.spinner_listview,
                        R.id.tvSpinnerList, babyInfos);
        spBabyInfo.setAdapter(babyInfoArrayAdapter);
        return view;
    }

    @Override
    public void onClick(View v) {
        String vaccineType, vaccineDetails;
        Long date;
        BabysInfo.BabyInfo babyInfo = (BabysInfo.BabyInfo)spBabyInfo.getSelectedItem();
        vaccineType = etVType.getText().toString();
        vaccineDetails = etVDetails.getText().toString();
        date = Utility.getDate();
        if (date < babyInfo.mDob) {
            Toast.makeText(getContext(), "Invalid Date", Toast.LENGTH_LONG).show();
            etDate.setTextColor(Color.RED);
            return;
        }
        GrowthDataProvider dp = GrowthDataProvider.get();
        if (dp.addVaccinationInfo("", vaccineDetails, date, babyInfo.mId) > -1 ) {
            Toast.makeText(getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
            EventsInfo info = EventsInfo.get(babyInfo.mId);
            if ( info == null) {
                info = EventsInfo.create(babyInfo.mId);
            }
            info.update();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
