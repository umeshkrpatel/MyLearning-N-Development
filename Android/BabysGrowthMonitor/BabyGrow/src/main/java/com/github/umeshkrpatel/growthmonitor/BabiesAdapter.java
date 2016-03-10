package com.github.umeshkrpatel.growthmonitor;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.umeshkrpatel.growthmonitor.data.IAdapter;
import com.github.umeshkrpatel.growthmonitor.data.IBabyInfo;
import com.github.umeshkrpatel.growthmonitor.data.IDataInfo;

import java.util.ArrayList;

/*
 * Created by umpatel on 2/9/2016.
 */
public class BabiesAdapter extends IAdapter {
    private ArrayList<IBabyInfo> mValues;
    private final IInfoFragment mListener;
    public BabiesAdapter(ArrayList<IBabyInfo> items,
                         IInfoFragment listener) {
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
        holder.mAgeView.setText(
            Utility.fromMilliSecondsToAge(holder.mItem.getBirthDate())
        );
        holder.mTimeline.setImageResource(IBabyInfo.getBabyImage(gen, age));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends IAdapter.IViewHolder
      implements View.OnLongClickListener, View.OnTouchListener, View.OnClickListener {
        public final View mView;
        public final View mBackgroundView, mForegroundView;
        public final TextView mInfoView, mAgeView;
        public final ImageView mTimeline;
        public IBabyInfo mItem;
        private float X1 = 0, X2 = 0;
        private static final float sDistance = 200;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mForegroundView = view.findViewById(R.id.viewForeground);
            mInfoView = (TextView) view.findViewById(R.id.blvBabyName);
            mAgeView = (TextView) view.findViewById(R.id.blvBabyAge);
            mTimeline = (ImageView) view.findViewById(R.id.blvBabyImage);
            mView.setOnLongClickListener(this);
            mView.setOnTouchListener(this);
            mBackgroundView = view.findViewById(R.id.viewBackground);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mInfoView.getText() + "'";
        }

        @Override
        public boolean onLongClick(View v) {
            changeView();
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                X1 = event.getX();
            } else if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
                X2 = X1 - event.getX();
                if (Math.abs(X2) > sDistance) {
                    if (X2 > 0) {
                        Toast.makeText(mView.getContext(), "LeftToRight", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mView.getContext(), "RightToLeft", Toast.LENGTH_LONG).show();
                    }
                }
            }
            return false;
        }

        private void changeView() {
            if (mBackgroundView.getVisibility() != View.VISIBLE) {
                mView.setBackgroundResource(R.drawable.sg_bg_editor);
                mBackgroundView.setVisibility(View.VISIBLE);
                mForegroundView.setVisibility(View.GONE);
                ImageView edit = (ImageView) mView.findViewById(R.id.itemEdit);
                edit.setOnClickListener(this);
                ImageView delete = (ImageView) mView.findViewById(R.id.itemDelete);
                delete.setOnClickListener(this);
                ImageView back = (ImageView) mView.findViewById(R.id.itemBack);
                back.setOnClickListener(this);
            } else {
                mBackgroundView.setVisibility(View.GONE);
                mForegroundView.setVisibility(View.VISIBLE);
                IBabyInfo.GenType gen = mItem.getGender();
                mView.setBackgroundResource(IBabyInfo.getBackground(gen));
            }
        }

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.itemEdit: {
                    if (mListener != null)
                        mListener.onBabyInfoInteraction(mItem.getId(), IDataInfo.ACTION_UPDATE);
                }
                break;

                case R.id.itemDelete: {
                    String msg = ResourceReader.getString(R.string.deleteBabyConfirm);
                    msg = String.format(msg, mItem.getName());
                    new AlertDialog.Builder(mView.getContext())
                      .setTitle(android.R.string.dialog_alert_title).setMessage(msg)
                      .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              if (mListener != null) {
                                  mListener.onBabyInfoInteraction(
                                    mItem.getId(), IDataInfo.ACTION_DELETE
                                  );
                              }
                              mValues = IBabyInfo.getBabyInfoList();
                              notifyItemRemoved(getAdapterPosition());
                          }
                      })
                      .setNegativeButton(R.string.no, null)
                      .show();
                }
                break;

                case R.id.itemBack: {
                    mBackgroundView.setVisibility(View.GONE);
                    mForegroundView.setVisibility(View.VISIBLE);
                    IBabyInfo.GenType gen = mItem.getGender();
                    mView.setBackgroundResource(IBabyInfo.getBackground(gen));
                }
                break;
            }
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
