package com.xmd.manager.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/9/22.
 */
public class AutoRecyclerView extends RecyclerView {

    public ScrollView parentScrollView;
    public int HeaderId;

    public int ContentContainerId;
    private int lastScrollData = 0;
    private boolean flag = false;

    public AutoRecyclerView(Context context) {
        this(context, null);
    }

    public AutoRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AutoRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(flag);
    }

    public void resume() {
        overScrollBy(0, -lastScrollData, 0, getScrollY(), 0, getScrollRange(), 0, 0, true);
        lastScrollData = 0;
    }

    int mTop = 10;

    public void scrollTo(View targetView) {
        int oldScrollY = getScrollY();
        int top = targetView.getTop() - mTop;
        int dataY = top - oldScrollY;
        lastScrollData = dataY;
        overScrollBy(0, dataY, 0, getScrollY(), 0, getScrollRange(), 0, 0, true);
    }

    private int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0, child.getHeight() - (getHeight()));
        }
        return scrollRange;
    }

    int currentY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (parentScrollView == null) {

            return super.onInterceptTouchEvent(e);

        } else {
            View parentChild = parentScrollView.getChildAt(0);
            int height2 = parentChild.getMeasuredHeight();
            height2 = height2 - parentScrollView.getMeasuredHeight();

            int scrollY2 = parentScrollView.getScrollY();

            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                if (scrollY2 >= height2) {
                    currentY = (int) e.getY();
                    setParentScrollAble(false);
                }
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                setParentScrollAble(true);
            } else if (e.getAction() == MotionEvent.ACTION_MOVE) {

            }
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {


        if (parentScrollView != null) {
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                int y = (int) ev.getY();

                View parentChild = parentScrollView.getChildAt(0);
                int height2 = parentChild.getMeasuredHeight();
                height2 = height2 - parentScrollView.getMeasuredHeight();

                int scrollY2 = parentScrollView.getScrollY();

                if (scrollY2 >= height2) {
                    if (currentY < y) {
                        boolean result = false;
                        LayoutManager manager = getLayoutManager();
                        if (manager instanceof GridLayoutManager) {
                            GridLayoutManager gm = (GridLayoutManager) manager;
                            result = gm.findViewByPosition(gm.findFirstVisibleItemPosition()).getTop() == 0 && gm.findFirstVisibleItemPosition() == 0;
                        } else if (manager instanceof LinearLayoutManager) {
                            LinearLayoutManager lm = (LinearLayoutManager) manager;
                            result = lm.findViewByPosition(lm.findFirstVisibleItemPosition()).getTop() == 0 && lm.findFirstVisibleItemPosition() == 0;
                        }
                        if (result) {
                            setParentScrollAble(true);
                            return false;
                        } else {
                            setParentScrollAble(false);
                        }
                    }
                    currentY = y;
                }
            }
        }
        return super.onTouchEvent(ev);
    }

    private void setParentScrollAble(boolean flag) {

        parentScrollView.requestDisallowInterceptTouchEvent(!flag);
    }
}
