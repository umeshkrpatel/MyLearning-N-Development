package com.github.umeshkrpatel.growthmonitor;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class BabiesListAdapter extends RecyclerView.Adapter<BabiesListAdapter.ViewHolder> {

    private final ArrayList<BabysInfo.BabyInfo> mValues;
    private final GrowthActivity mListener;

    public BabiesListAdapter(ArrayList<BabysInfo.BabyInfo> items, GrowthActivity listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_babieslist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mInfoView.setText(holder.mItem.mName + (holder.mItem.mGender==0?"(Girl)":"(Boy)"));
        holder.mInfoView.setText(
                generateSpannableText(
                        Utility.getDateTimeFromMillisecond(holder.mItem.mDob),
                        holder.mItem.mName,
                        (holder.mItem.mGender)
                )
        );
        holder.mDateView.setText(Utility.getDateTimeFromMillisecond(holder.mItem.mDob));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mInfoView;
        public final TextView mDateView;
        public BabysInfo.BabyInfo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mInfoView = (TextView) view.findViewById(R.id.blInfo);
            mDateView = (TextView) view.findViewById(R.id.blDate);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mInfoView.getText() + "'";
        }
    }

    private static SpannableString generateSpannableText(String first, String second, String third) {
        int flen = first.length(), slen = second.length(), tlen = third.length();
        SpannableString s = new SpannableString(first + '\n' + second + '\n' + third);
        s.setSpan(new RelativeSizeSpan(1f), 0, flen, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, flen, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), flen + 1, flen + slen + 1, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), flen + 1, flen + slen + 1, 0);
        s.setSpan(new RelativeSizeSpan(.8f), flen + 1, flen + slen + 1, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), flen + slen + 1, flen + slen + tlen + 1, 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), flen + slen + 1, flen + slen + tlen + 2, 0);
        return s;
    }
}
