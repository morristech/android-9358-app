package com.xmd.cashier.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zr on 17-4-19.
 * 每次只显示当前列表的第一个Item
 * 不可滑动
 */

public class CustomNotifyLayoutManager extends RecyclerView.LayoutManager {
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0) {
            return;
        }
        detachAndScrapAttachedViews(recycler);
        View view = recycler.getViewForPosition(0);
        measureChildWithMargins(view, 0, 0);
        addView(view);
        int height = getDecoratedMeasuredHeight(view);
        int width = getDecoratedMeasuredWidth(view);
        layoutDecorated(view, 0, 0, width, height);
    }
}
