package com.github.umeshkrpatel.growthmonitor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.umeshkrpatel.growthmonitor.data.IAdapter;
import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IEventInfo;
import com.github.umeshkrpatel.growthmonitor.data.ItemCallbackHelper;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link IInfoFragment}
 * interface.
 */
public class GrowthFragment extends Fragment {

    @Nullable
    private static IInfoFragment mListener;
    private RecyclerView mView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public static GrowthFragment instance;

    public static GrowthFragment getOrCreate(IInfoFragment listener) {
        if (listener != null)
            mListener = listener;
        if (instance == null)
            instance = new GrowthFragment();
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventtimeline_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mView = (RecyclerView) view;
            mView.setLayoutManager(new LinearLayoutManager(context));
            int babyId = IBabyInfo.currentBabyInfo().getId();
            ArrayList<IEventInfo> eventItems = IEventInfo.create(babyId);
            mView.setAdapter(new TimelineAdapter(eventItems, mListener));
            ItemCallbackHelper.attachToRecyclerView(mView);
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setAdapter(IAdapter adapter) {
        if (mView != null) {
            mView.setAdapter(adapter);
        }
    }

    public void update() {
        if (mView != null) {
            int babyId = IBabyInfo.currentBabyInfo().getId();
            ArrayList<IEventInfo> eventItems = IEventInfo.get(babyId);
            mView.setAdapter(new TimelineAdapter(eventItems, mListener));
        }
    }
}
