package com.xmd.technician.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by sdcm on 16-4-7.
 */
public class HeightListView extends ListView {

    public HeightListView(Context context) {
        super(context);
    }

    public HeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_MOVE)
        {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }
}
