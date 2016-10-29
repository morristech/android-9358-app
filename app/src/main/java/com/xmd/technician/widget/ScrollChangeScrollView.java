package com.xmd.technician.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.xmd.technician.common.ObservableScrollable;
import com.xmd.technician.common.OnScrollChangedCallback;

/**
 * Created by Administrator on 2016/10/28.
 */
public class ScrollChangeScrollView extends ScrollView implements ObservableScrollable{

    private OnScrollChangedCallback mOnScrollChangedListener;
    private boolean mDisableEdgeEffects = true;

    public ScrollChangeScrollView(Context context) {
        super(context);
    }

    public ScrollChangeScrollView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public ScrollChangeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
    }



    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if (x == 0 && y == 0 || y <= 0) {
            super.scrollTo(x, y);
        }
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnScrollChangedListener != null) {
            mOnScrollChangedListener.onScroll(l, t);
        }
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        if (mDisableEdgeEffects
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return 0.0f;
        }
        return super.getTopFadingEdgeStrength();
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        if (mDisableEdgeEffects && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return 0.0f;
        }
        return super.getBottomFadingEdgeStrength();
    }

    @Override
    public void setOnScrollChangedCallback(OnScrollChangedCallback callback) {
        mOnScrollChangedListener = callback;
    }
}
