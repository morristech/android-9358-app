package com.xmd.technician.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sdcm on 16-5-18.
 */
public class PhotoGridLayoutManager extends GridLayoutManager {

    private int mSpanCount;

    public PhotoGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        mSpanCount = spanCount;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        int height = 0;

        for (int i = 0; i < getItemCount(); ) {
            height = height + widthSize / mSpanCount;
            i = i + mSpanCount;
        }
        // If child view is more than screen size, there is no need to make it wrap content. We can use original onMeasure() so we can scroll view.
        setMeasuredDimension(widthSize, height);
    }
}
