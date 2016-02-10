package com.github.umeshkrpatel.growthmonitor;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.umeshkrpatel.growthmonitor.data.IAdapter;

import java.util.ArrayList;

/**
 * Created by umpatel on 2/9/2016.
 */
public class BabiesAdapter extends IAdapter {
    private final ArrayList<BabiesInfo.BabyInfo> mValues;
    private final GrowthActivity mListener;
    public BabiesAdapter(ArrayList<BabiesInfo.BabyInfo> items, GrowthActivity listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public IViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.babies_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IViewHolder iholder, int position) {
        final ViewHolder holder = (ViewHolder)iholder;
        holder.mItem = mValues.get(position);
        holder.mInfoView.setText(holder.mItem.getName());
        int gen = holder.mItem.getGender().equals("Girl")?1:0;
        float age = Utility.fromMiliSecondsToMonths(
                System.currentTimeMillis() - holder.mItem.getDob());
        holder.mTimeline.setImageResource(BabiesInfo.getBabyImage(gen, age));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends IAdapter.IViewHolder implements View.OnClickListener {
        @NonNull
        public final View mView;
        @NonNull
        public final TextView mInfoView;
        //@NonNull
        //public final TextView mDate;
        @NonNull
        public final ImageView mTimeline;
        public BabiesInfo.BabyInfo mItem;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mInfoView = (TextView) view.findViewById(R.id.blvBabyName);
            mTimeline = (ImageView) view.findViewById(R.id.blvBabyImage);
            mView.setOnClickListener(this);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mInfoView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            mListener.onBabyInfoInteraction(mItem);
        }
    }
}
