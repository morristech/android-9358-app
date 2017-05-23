package com.xmd.technician.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by sdcm on 17-3-28.
 */

public class BlockChildLinearLayout extends LinearLayout {
    public BlockChildLinearLayout(Context context) {
        super(context);
    }

    public BlockChildLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockChildLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BlockChildLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
