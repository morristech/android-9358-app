package com.xmd.app.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Lhj on 17-7-7.
 */

public class LimitHeightRecyclerView extends LinearLayoutManager{


    public LimitHeightRecyclerView(Context context) {
        super(context);
    }

    public LimitHeightRecyclerView(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LimitHeightRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        if(this.getChildCount()>0){
            View firstChildView = recycler.getViewForPosition(0);
            measureChild(firstChildView,widthSpec,heightSpec);
            setMeasuredDimension(View.MeasureSpec.getSize(widthSpec),firstChildView.getMeasuredHeight()*4);
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);

    }
}
