package com.xmd.manager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.common.ResourceUtils;

/**
 * Created by linms@xiaomodo.com on 16-5-30.
 */
public class SelectorTextView extends TextView {

    private static final int DEFAULT_SELECTED_WIDTH = 14;

    private Paint mPaint;
    private int mSelectedColor;
    private int mSelectedBackground;
    private int mSelectedWidth;

    public SelectorTextView(Context context) {
        this(context, null);
    }

    public SelectorTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SelectorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SelectorTextView);
        mSelectedWidth = a.getDimensionPixelSize(R.styleable.SelectorTextView_selected_width, DEFAULT_SELECTED_WIDTH);
        mSelectedBackground = a.getColor(R.styleable.SelectorTextView_selected_background, ResourceUtils.getColor(R.color.tab_bg));
        mSelectedColor = a.getColor(R.styleable.SelectorTextView_selected_color, ResourceUtils.getColor(R.color.primary_color));
        a.recycle();

        mPaint = new Paint();
        mPaint.setColor(mSelectedColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isSelected()) {
            canvas.drawColor(mSelectedBackground);
            int height = getMeasuredHeight();
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            canvas.drawRect((float) 0, (float) paddingTop, (float) mSelectedWidth, (float) (height - paddingBottom), mPaint);
        }
        super.onDraw(canvas);
    }
}
