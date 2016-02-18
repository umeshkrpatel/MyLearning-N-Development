package com.github.umeshkrpatel.growthmonitor;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.umeshkrpatel.growthmonitor.data.IAdapter;
import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

import java.util.ArrayList;

/**
 * Created by umpatel on 2/9/2016.
 */
public class BabiesAdapter extends IAdapter {
    private ArrayList<IBabyInfo> mValues;
    private final GrowthActivity mListener;
    public BabiesAdapter(ArrayList<IBabyInfo> items, GrowthActivity listener) {
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
        IBabyInfo.GenType gen = holder.mItem.getGender();

        holder.mInfoView.setText(generateSpannableText(holder.mItem.getName(), gen.toString()));
        holder.mView.setBackgroundResource(IBabyInfo.getBackground(gen));

        float age = Utility.fromMiliSecondsToMonths(
                System.currentTimeMillis() - holder.mItem.getBirthDate());
        holder.mAgeView.setText(Utility.fromMilliSecondsToAge(
                System.currentTimeMillis() - holder.mItem.getBirthDate()));
        holder.mTimeline.setImageResource(IBabyInfo.getBabyImage(gen, age));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends IAdapter.IViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView mInfoView, mAgeView;
        public final ImageView mTimeline, mBabyRemove;
        public IBabyInfo mItem;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mInfoView = (TextView) view.findViewById(R.id.blvBabyName);
            mAgeView = (TextView) view.findViewById(R.id.blvBabyAge);
            mTimeline = (ImageView) view.findViewById(R.id.blvBabyImage);
            mBabyRemove = (ImageView) view.findViewById(R.id.blvBabyRemove);
            mView.setOnClickListener(this);
            mBabyRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onBabyInfoInteraction(mItem.getId(), IDataInfo.ACTION_DELETE);
                    mValues = IBabyInfo.getBabyInfoList();
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mInfoView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            mListener.onBabyInfoInteraction(mItem.getId(), IDataInfo.ACTION_UPDATE);
        }
    }
    @NonNull
    private static SpannableString generateSpannableText(
            @NonNull String name, @NonNull String gen) {
        int flen = name.length();
        SpannableString s = new SpannableString(name + '(' + gen + ')');
        s.setSpan(new RelativeSizeSpan(1f), 0, flen, 0);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, flen, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), flen, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.MAGENTA), flen, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(.6f), flen, s.length(), 0);
        return s;
    }
}
