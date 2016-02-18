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

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GrowthFragment extends Fragment {

    @Nullable
    private GrowthActivity mListener;
    private RecyclerView mView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public static GrowthFragment instance;

    public static GrowthFragment get() {
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
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onEventInfoInteraction(IEventInfo item);
        void onBabyInfoInteraction(int babyId, int action);
    }

    public void setAdapter(IAdapter adapter) {
        mView.setAdapter(adapter);
    }

    public void update() {
        int babyId = IBabyInfo.currentBabyInfo().getId();
        ArrayList<IEventInfo> eventItems = IEventInfo.get(babyId);
        mView.setAdapter(new TimelineAdapter(eventItems, mListener));
    }
}
