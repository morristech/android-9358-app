package com.xmd.app.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Lhj on 17-7-19.
 */

public class StationaryScrollView extends ScrollView {
    public StationaryScrollView(Context context) {
        super(context);
    }

    public StationaryScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StationaryScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StationaryScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
