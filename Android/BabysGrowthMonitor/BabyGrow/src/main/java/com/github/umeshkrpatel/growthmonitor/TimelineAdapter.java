package com.github.umeshkrpatel.growthmonitor;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.umeshkrpatel.growthmonitor.data.IAdapter;
import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IEventInfo;

import java.util.ArrayList;

public class TimelineAdapter extends IAdapter {

    private final ArrayList<IEventInfo> mValues;
    private final GrowthActivity mListener;
    private static final int[] sTimeLineImageIDs = new int[] {
        R.drawable.life_newborn, R.drawable.life_3months, R.drawable.life_6months,
        R.drawable.life_1years, R.drawable.life_2years
    };

    public static int getTimelineImage(long date, long dob) {
        float months = Utility.fromMiliSecondsToMonths(date - dob);
        int timelineId;
        if (months <= 3)
            timelineId = 0;
        else if (months <= 6)
            timelineId = 1;
        else if (months <= 12)
            timelineId = 2;
        else if (months <= 24)
            timelineId = 3;
        else
            timelineId = 4;
        return sTimeLineImageIDs[timelineId];
    }

    public TimelineAdapter(ArrayList<IEventInfo> items, GrowthActivity listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_listview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final IViewHolder iholder, int position) {
        final ViewHolder holder = (ViewHolder)iholder;
        holder.mItem = mValues.get(position);

        IBabyInfo.GenType gen = IBabyInfo.currentBabyInfo().getGender();
        holder.mView.setBackgroundResource(IBabyInfo.getBackground(gen));

        holder.mInfoView.setText(
            generateSpannableText(
                Utility.getDateTimeFromMillisecond(holder.mItem.getDate()),
                IEventInfo.getEventDetails(holder.mItem),
                ""));
        holder.mTimeline.setImageResource(
            getTimelineImage(
                holder.mItem.getDate(),
                IBabyInfo.currentBabyInfo().getBirthDate()
            ));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onEventInfoInteraction(holder.mItem);
            }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends IAdapter.IViewHolder {
        @NonNull
        public final View mView;
        @NonNull
        public final TextView mInfoView;
        //public final TextView mDateView;
        @NonNull
        public final ImageView mTimeline;
        public IEventInfo mItem;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mInfoView = (TextView) view.findViewById(R.id.blInfo);
            mTimeline = (ImageView) view.findViewById(R.id.ivTimeline);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mInfoView.getText() + "'";
        }
    }

    @NonNull
    private static SpannableString generateSpannableText(
            @NonNull String first, @NonNull SpannableStringBuilder second, @NonNull String third) {
        int flen = first.length(), slen = second.length(), tlen = third.length();
        SpannableString s = new SpannableString(first + '\n' + second + '\n' + third);
        s.setSpan(new RelativeSizeSpan(1f), 0, flen, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, flen, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), flen + 1, flen + slen + 1, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), flen + 1, flen + slen + 1, 0);
        s.setSpan(new RelativeSizeSpan(.8f), flen + 1, flen + slen + 1, 0);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), flen + 1, flen + slen + 1, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), flen + slen + 1, flen + slen + tlen + 1, 0);
        s.setSpan(new ForegroundColorSpan(
            ColorTemplate.getHoloBlue()), flen + slen + 1, flen + slen + tlen + 2, 0);
        return s;
    }
}
