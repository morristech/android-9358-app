package com.xmd.manager.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.security.InvalidParameterException;

/**
 * Created by heyangya on 16-9-5.
 */

public class CustomRecycleViewDecoration extends RecyclerView.ItemDecoration {
    public final static int ORIENTATION_VERTICAL = 0;
    public final static int ORIENTATION_HORIZONTAL = 1;
    private int mOrientation;
    private int mDivideSize;

    public CustomRecycleViewDecoration(int divideSize) {
        mOrientation = ORIENTATION_VERTICAL;
        mDivideSize = divideSize;
    }

    public CustomRecycleViewDecoration(int divideSize, int orientation) {
        if (orientation != ORIENTATION_HORIZONTAL && orientation != ORIENTATION_VERTICAL) {
            throw new InvalidParameterException("invalid orientation!");
        }
        mOrientation = orientation;
        mDivideSize = divideSize;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == ORIENTATION_VERTICAL) {
            outRect.set(0, 0, 0, mDivideSize);
        } else {
            outRect.set(0, 0, mDivideSize, 0);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }
}
