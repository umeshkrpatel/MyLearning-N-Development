package com.github.umeshkrpatel.growthmonitor.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/*
 * Created by weumeshweta on 06-Mar-2016.
 */
public class ItemCallbackHelper extends ItemTouchHelper.Callback {
    private static ItemCallbackHelper mItemCallbackHelper = null;
    private static ItemTouchHelper mItemTouchHelper = null;
    public static ItemTouchHelper getOrCreateHelper() {
        if (mItemTouchHelper == null) {
            mItemTouchHelper = new ItemTouchHelper(getOrCreate());
        }
        return mItemTouchHelper;
    }

    public static ItemCallbackHelper getOrCreate() {
        if (mItemCallbackHelper == null) {
            mItemCallbackHelper = new ItemCallbackHelper();
        }
        return mItemCallbackHelper;
    }
    public static void attachToRecyclerView(RecyclerView view) {
        getOrCreateHelper().attachToRecyclerView(view);
    }

    private ItemCallbackHelper() {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && Math.abs(dX) > 200) {
            View itemView = viewHolder.itemView;

            Paint paint = new Paint();
            /*
            if (dX > 0) { // swiping right
                paint.setColor(Color.YELLOW);
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                    (float) itemView.getBottom(), paint);
            } else { // swiping left
                paint.setColor(Color.RED);
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                    (float) itemView.getRight(), (float) itemView.getBottom(), paint);
            }
            */
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
