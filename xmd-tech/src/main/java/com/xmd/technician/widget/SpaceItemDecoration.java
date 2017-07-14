package com.xmd.technician.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

/**
 * Created by sdcm on 16-5-18.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    public static final int GRID_LAYOUT = 0;

    public static final int LINE_LAYOUT = 1;

    private int mOrientation;
    private int mSpace;

    public SpaceItemDecoration(int space, int orientation) {
        this.mSpace = space;
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != GRID_LAYOUT && orientation != LINE_LAYOUT) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (mOrientation == LINE_LAYOUT) {
            outRect.set(0, 0, 0, mSpace);
        } else {
            outRect.set(0, mSpace, mSpace, 0);
        }
    }
}
