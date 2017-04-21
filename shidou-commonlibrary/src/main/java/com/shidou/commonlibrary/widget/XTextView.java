package com.shidou.commonlibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by mo on 16-3-27.
 * 增加drawable点击事件
 */
public class XTextView extends android.widget.TextView {
    private OnClickListener mDrawableClickListenerLeft;
    private OnClickListener mDrawableClickListenerRight;
    private OnClickListener mDrawableClickListenerTop;
    private OnClickListener mDrawableClickListenerBottom;

    static final int DRAWABLE_LEFT = 0;
    static final int DRAWABLE_TOP = 1;
    static final int DRAWABLE_RIGHT = 2;
    static final int DRAWABLE_BOTTOM = 3;

    private Rect mTextViewRect;

    public XTextView(Context context) {
        super(context);
    }

    public XTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDrawableClickListenerLeft(OnClickListener listenerLeft) {
        mDrawableClickListenerLeft = listenerLeft;
    }

    public void setDrawableClickListenerRight(OnClickListener listenerRight) {
        mDrawableClickListenerRight = listenerRight;
    }

    public void setDrawableClickListenerTop(OnClickListener listenerTop) {
        mDrawableClickListenerTop = listenerTop;
    }

    public void setDrawableClickListenerBottom(OnClickListener listenerBottom) {
        mDrawableClickListenerBottom = listenerBottom;
    }

    private boolean processTouchEvent(int drawableType, OnClickListener listener, MotionEvent event) {
        if (listener == null)
            return false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        Rect rect = getPosition(drawableType);
        if (rect.left <= x && x <= rect.right && rect.top <= y && y <= rect.bottom) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                listener.onClick(this);
            }
            return true;
        }
        return false;
    }

    private Rect getPosition(int drawableType) {
        Drawable drawable = getCompoundDrawables()[drawableType];
        Rect rect = drawable.getBounds();
        switch (drawableType) {
            case DRAWABLE_RIGHT:
                rect.left = mTextViewRect.right - rect.width();
                rect.right = mTextViewRect.right;
                break;
            case DRAWABLE_BOTTOM:
                rect.top = mTextViewRect.bottom - rect.height();
                rect.bottom = mTextViewRect.bottom;
                break;
        }
        return rect;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (processTouchEvent(DRAWABLE_LEFT, mDrawableClickListenerLeft, event)) {
            return true;
        }
        if (processTouchEvent(DRAWABLE_TOP, mDrawableClickListenerTop, event)) {
            return true;
        }
        if (processTouchEvent(DRAWABLE_RIGHT, mDrawableClickListenerRight, event)) {
            return true;
        }
        if (processTouchEvent(DRAWABLE_BOTTOM, mDrawableClickListenerBottom, event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTextViewRect == null) {
            mTextViewRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
        } else {
            mTextViewRect.set(getLeft(), getTop(), getRight(), getBottom());
        }
    }
}
