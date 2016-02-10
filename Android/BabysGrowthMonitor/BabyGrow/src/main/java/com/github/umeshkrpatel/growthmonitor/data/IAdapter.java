package com.github.umeshkrpatel.growthmonitor.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by umpatel on 2/9/2016.
 */
public abstract class IAdapter extends RecyclerView.Adapter<IAdapter.IViewHolder> {

    public abstract class IViewHolder extends RecyclerView.ViewHolder {
        public IViewHolder(View itemView) {
            super(itemView);
        }
    }
}
