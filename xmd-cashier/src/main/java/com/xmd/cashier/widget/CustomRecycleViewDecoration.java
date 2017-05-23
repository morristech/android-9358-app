package com.xmd.cashier.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by heyangya on 16-9-5.
 */

public class CustomRecycleViewDecoration extends RecyclerView.ItemDecoration {
    private int mDivideHeight;

    public CustomRecycleViewDecoration(int divideHeight) {
        mDivideHeight = divideHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, mDivideHeight);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }
}
