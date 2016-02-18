package com.github.umeshkrpatel.growthmonitor.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.umeshkrpatel.growthmonitor.R;

import java.util.ArrayList;

/**
 * Created by weumeshweta on 14-Feb-2016.
 */
public class ISpinnerAdapter extends ArrayAdapter<IBabyInfo> {

    private final Context ctx;
    private String[] contentArray;
    private ArrayList<IBabyInfo> conA;
    //private int[] imageArray;

    public ISpinnerAdapter(Context context, int resource, ArrayList<IBabyInfo> objects,
                           int[] imageArray) {
        super(context, R.layout.spinner_babylist, R.id.tvSpinnerList, objects);
        this.ctx = context;
        this.conA = objects;
        //this.imageArray = imageArray;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_babylist, parent, false);

        TextView textView = (TextView) row.findViewById(R.id.tvSpinnerList);
        textView.setText(conA.get(position).getName());

        ImageView imageView = (ImageView)row.findViewById(R.id.ivSpinnerList);
        imageView.setImageResource(R.drawable.ic_face_girl);

        return row;
    }
}
