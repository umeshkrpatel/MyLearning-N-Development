package com.github.umeshkrpatel.growthmonitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;
import com.github.umeshkrpatel.growthmonitor.data.IEventInfo;

import java.util.ArrayList;

public class TimelineAdapter extends IAdapter {

    private ArrayList<IEventInfo> mValues;
    private final IInfoFragment mListener;
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

    public TimelineAdapter(ArrayList<IEventInfo> items,
                           IInfoFragment listener) {
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
            IEventInfo.getEventDetails(holder.mItem), ""
          )
        );
        holder.mTimeline.setImageResource(
          getTimelineImage(
            holder.mItem.getDate(),
            IBabyInfo.currentBabyInfo().getBirthDate()
          )
        );
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends IAdapter.IViewHolder
      implements View.OnLongClickListener, View.OnClickListener {
        @NonNull
        public final View mView, mBackground, mForeground;
        @NonNull
        public final TextView mInfoView;
        @NonNull
        public final ImageView mTimeline;
        public IEventInfo mItem;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mBackground = view.findViewById(R.id.viewBackground);
            mForeground = view.findViewById(R.id.viewForeground);
            mInfoView = (TextView) view.findViewById(R.id.blInfo);
            mTimeline = (ImageView) view.findViewById(R.id.ivTimeline);
            mView.setOnLongClickListener(this);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mInfoView.getText() + "'";
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItem.getEventType() != IDataInfo.EVENT_LIFEEVENT)
                changeView();
            return true;
        }

        private void changeView() {
            if (mBackground.getVisibility() != View.VISIBLE) {
                mView.setBackgroundResource(R.drawable.sg_bg_editor);
                mBackground.setVisibility(View.VISIBLE);
                mForeground.setVisibility(View.GONE);
                ImageView edit = (ImageView) mView.findViewById(R.id.itemEdit);
                edit.setOnClickListener(this);
                ImageView delete = (ImageView) mView.findViewById(R.id.itemDelete);
                delete.setOnClickListener(this);
                ImageView back = (ImageView) mView.findViewById(R.id.itemBack);
                back.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.itemEdit: {
                    if (null != mListener) {
                        mListener.onEventInfoInteraction(mItem, IDataInfo.ACTION_UPDATE);
                    }
                }
                break;

                case R.id.itemDelete: {
                    String msg = ResourceReader.getString(R.string.deleteBabyConfirm);
                    msg = String.format(msg, IBabyInfo.currentBabyInfo().getName());
                    new AlertDialog.Builder(mView.getContext())
                      .setTitle(android.R.string.dialog_alert_title).setMessage(msg)
                      .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              if (mListener != null) {
                                  mListener.onEventInfoInteraction(
                                    mItem, IDataInfo.ACTION_DELETE
                                  );
                              }
                              mValues = IEventInfo.get(-1);
                              notifyItemRemoved(getAdapterPosition());
                          }
                      })
                      .setNegativeButton(R.string.no, null)
                      .show();
                }
                break;

                case R.id.itemBack: {
                    mBackground.setVisibility(View.GONE);
                    mForeground.setVisibility(View.VISIBLE);
                    IBabyInfo.GenType gen = IBabyInfo.currentBabyInfo().getGender();
                    mView.setBackgroundResource(IBabyInfo.getBackground(gen));
                }
                break;
            }
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
