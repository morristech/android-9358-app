package com.xmd.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Lhj on 17-7-19.
 */

public class StationaryScrollView extends NestedScrollView{
    public StationaryScrollView(Context context) {
        super(context);
    }

    public StationaryScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StationaryScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
